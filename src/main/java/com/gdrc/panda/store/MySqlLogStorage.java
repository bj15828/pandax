package com.gdrc.panda.store;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gdrc.panda.Config;
import com.gdrc.panda.CorePeer;
import com.gdrc.panda.PandaException;
import com.gdrc.panda.tran.StateObject;
import com.gdrc.panda.tran.Transaction;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class MySqlLogStorage implements ILogStorage {

  Logger logger = LoggerFactory.getLogger(MySqlLogStorage.class);

  HikariDataSource ds;

  CorePeer peer;

  String selectPara = " uuid,peer,last_term,last_index,trans,commit_flag ";

  String commit_block_update =
      "update logs set commit_flag = 1 where peer = ? and last_term = ? and last_index = ?";

  String sql_transfer = "SELECT balance FROM ledger WHERE account=? or account = ? for update";
  String update = "update ledger set balance = ? where account = ?";
  String insert_sql =
      "INSERT INTO logs(uuid,peer,last_term,last_index,trans,create_time,pre_term,pre_index,commit_flag) VALUES(?,?,?,?,?,?,?,?,?) ";

  String sql_select_by_term_index =
      "SELECT " + selectPara + "  FROM logs WHERE last_term=? and last_index =?";

  String sql_by_uuid = "SELECT " + selectPara + " FROM logs WHERE uuid=?";

  String select_peer_last_append_block =
      "select  " + selectPara + "  from logs where peer = ?   order by create_time desc limit 1";
  
  String select_between = "select "+ selectPara +  " from logs where last_term >=? and last_term <= ? and last_index  >= ? and last_index <= ? order by create_time desc";

  public MySqlLogStorage(CorePeer peer, Config cfg) throws PandaException {

    this.peer = peer;

    String pre = cfg.get("log_storage.impl");

    int maxStoreLogSize = cfg.getInt("raft.command_max_size");
    String user = cfg.get(pre + ".user");
    String password = cfg.get(pre + ".pwd");
    String ip = cfg.get(pre + ".ip");
    String port = cfg.get(pre + ".port");
    String db = cfg.get(pre + ".db");

    // If there's a store-specific setting, use it; otherwise use default
    String url =
        "jdbc:mysql://" + ip + ":" + port + "/" + db + "?useUnicode=true&characterEncoding=UTF-8";

    logger.info("Initializing MySql storage. maxCommandSize={} url={}", maxStoreLogSize, url);

    HikariConfig config = new HikariConfig();
    config.setJdbcUrl(url);
    config.setUsername(user);
    config.setPassword(password);
    config.addDataSourceProperty("cachePrepStmts", "true");
    config.addDataSourceProperty("prepStmtCacheSize", "250");
    config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

    ds = new HikariDataSource(config);
    try (Connection c = ds.getConnection()) {
      if (!c.getAutoCommit()) {
        throw new IllegalStateException("Auto-commit must be enabled.");
      }
      // Statement statement = c.createStatement();

      // TODO: change varchar to binary.
      /*
       * statement.execute(
       * "CREATE TABLE `logs` (  `uuid` varchar(32) COLLATE utf8_unicode_ci NOT NULL," +
       * "  `peer` varchar(20) COLLATE utf8_unicode_ci NOT NULL,  `last_term` int(11) NOT NULL," +
       * "`last_index` int(11) NOT NULL,`trans` varchar(500) COLLATE utf8_unicode_ci NOT NULL,"
       * pre_index , pre_term,
       * 
       * +
       * "`create_time` timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),"
       * + "PRIMARY KEY (`uuid`)," +
       * " KEY `search` (`peer`,`last_term`,`last_index`)) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;"
       * );
       */

    } catch (SQLException e) {
      throw new PandaException(e);
    }
  }

  @Override
  public LogEntry getLastLog() throws PandaException {

    

    Connection c;
    try {
      c = ds.getConnection();
    } catch (SQLException e1) {

      throw new PandaException(e1);
    }
    
    try  {

      PreparedStatement preparedStatement = c.prepareStatement(select_peer_last_append_block);
      logger.info("{}", this.select_peer_last_append_block);

      preparedStatement.setString(1, this.peer.getPeerName());

      ResultSet resultSet = preparedStatement.executeQuery();
      if (resultSet.next()) {

        LogEntry cmd = resultToCmd(resultSet);

        return cmd;
      }
      return null;
    } catch (SQLException e) {
      throw new PandaException(e);
    } finally {
      try {
        c.close();
      } catch (SQLException e) {
       
        e.printStackTrace();
      }
    }

  }

  @Override
  public boolean appendLog(LogEntry log) throws PandaException {

    LogEntry cmd1 = log;
    // cmd1.setPreLogTerm(1);
   


    Connection c;
    try {
      c = ds.getConnection();
    } catch (SQLException e1) {

      throw new PandaException(e1);
    }

    try {


      c.setAutoCommit(false);

      PreparedStatement preparedStatement = c.prepareStatement(insert_sql);
      preparedStatement.setString(1, cmd1.getUuid());
      preparedStatement.setString(2, cmd1.getPeer());
      preparedStatement.setLong(3, cmd1.getTerm());
      preparedStatement.setLong(4, cmd1.getLastIndex());

      if (null != cmd1.getData())
        preparedStatement.setString(5, cmd1.getData().toJson());
      else
        preparedStatement.setString(5, "");
      preparedStatement.setLong(6, System.currentTimeMillis());
      preparedStatement.setLong(7, cmd1.getPreLogTerm());
      preparedStatement.setLong(8, cmd1.getPreLogIndex());
      preparedStatement.setInt(9, cmd1.getHasCommit());

      int i = preparedStatement.executeUpdate();

      c.commit();
      if (i < 1) {
        throw new SQLException(String.format("save(peer=%d, term=%d, logindex=%d) failed",
            cmd1.getPeer(), cmd1.getTerm(), cmd1.getLastIndex()));

      }

    } catch (SQLException e) {

      if (null != c) {
        try {
          c.rollback();

        } catch (SQLException e1) {
          
          e.printStackTrace();
          throw new PandaException(e);
        }
      }

    } finally {

      try {
        c.close();
      } catch (SQLException e) {
       
        e.printStackTrace();
      }

    }
    logger.info("save term : {} index  :{}",log.getTerm(),log.getLastIndex());
    return true;

  }

  @Override
  public boolean commitLog(long term, long lastIndex) throws PandaException {


    Connection c;
    try {
      c = ds.getConnection();
    } catch (SQLException e1) {

      throw new PandaException(e1);
    }


    try {
      PreparedStatement psUpdate = c.prepareStatement(commit_block_update);

      psUpdate.setString(1, this.peer.getPeerName());
      psUpdate.setLong(2, term);
      psUpdate.setLong(3, lastIndex);

      return psUpdate.execute();
    } catch (SQLException e) {

      throw new PandaException(e);
    } finally {

      try {
        c.close();
      } catch (SQLException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }

    }

  }

  private LogEntry resultToCmd(ResultSet resultSet) throws SQLException {

    LogEntry cmd = new LogEntry();
    cmd.setUuid(resultSet.getString(1));

    StateObject obj = new Transaction();
    obj.fromJson(resultSet.getString(5));

    cmd.setData(obj);


    cmd.setTerm(resultSet.getLong(3));
    cmd.setLastIndex(resultSet.getLong(4));
    cmd.setPeer(resultSet.getString(2));
    cmd.setHasCommit(resultSet.getInt(5));


    return cmd;

  }

  @Override
  public LogEntry getLog(long term, long index) throws PandaException {

    Connection c;
    try {
      c = ds.getConnection();
    } catch (SQLException e1) {

      throw new PandaException(e1);
    }

    try {

      PreparedStatement preparedStatement = c.prepareStatement(sql_select_by_term_index);
      preparedStatement.setString(1, term + "");
      preparedStatement.setString(2, index + "");
      ResultSet resultSet = preparedStatement.executeQuery();
     
      if (resultSet.next()) {

        LogEntry cmd = resultToCmd(resultSet);

        return cmd;
      }
      return null;
    } catch (SQLException e) {
      throw new PandaException(e);
    } finally {
      try {
        c.close();

      } catch (SQLException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
  }

  @Override
  public boolean deleteLogFrom(long term, long logIndex) throws PandaException {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean isContain(long term, long lastIndex) throws PandaException {

    Connection c;
    try {
      logger.info("is contain get connection before");
      c = ds.getConnection();
      logger.info("is contain get connection after");
    } catch (SQLException e1) {

      throw new PandaException(e1);
      
    }
    
   
    
    try {

      PreparedStatement preparedStatement = c.prepareStatement(sql_select_by_term_index);
      preparedStatement.setString(1, term + "");
      preparedStatement.setString(2, lastIndex + "");
      ResultSet resultSet = preparedStatement.executeQuery();
      if (resultSet.next()) {



        return true;
      }else{
      return false;
      }
    } catch (SQLException e) {
      throw new PandaException(e);
    } finally {
      try {
        c.close();
      } catch (SQLException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    
    

  }
  public List getLogEntrysBetween(long startTerm, long startIndex,long endTerm ,long endIndex ) throws PandaException{
    
    Connection c;
    try {
      c = ds.getConnection();
    } catch (SQLException e1) {

      throw new PandaException(e1);
    }

    try {

      PreparedStatement preparedStatement = c.prepareStatement(select_between);
      
      preparedStatement.setLong(1, startTerm );
      preparedStatement.setLong(2, endTerm);
      preparedStatement.setLong(3, startIndex);
      preparedStatement.setLong(4, endIndex);
      ResultSet resultSet = preparedStatement.executeQuery();
    
    
     int i = 0 ;
     List<LogEntry> entries = new ArrayList();
      while (resultSet.next()) {

        
        entries.add( resultToCmd(resultSet) );
        
        i++;
        
      }
      return entries;
    } catch (SQLException e) {
      throw new PandaException(e);
    } finally {
      try {
        c.close();

      } catch (SQLException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    
  }

}
