package apros.codeart.ddd.repository.access;

import java.sql.Connection;
import java.util.UUID;

import apros.codeart.ddd.IAggregateRoot;
import apros.codeart.ddd.MapData;
import apros.codeart.ddd.QueryLevel;
import apros.codeart.ddd.repository.DataContext;
import apros.codeart.ddd.repository.access.internal.QueryRunner;
import apros.codeart.ddd.repository.access.internal.SqlStatement;
import apros.codeart.dto.DTObject;

public final class DataAccess {

	private Connection _conn;

	DataAccess(Connection conn) {
		_conn = conn;
	}

	/**
	 * 
	 * 执行sql，并且不复刻指令到其他服务器
	 * 
	 * @param sql
	 */
	public void execute(String sql) {
		QueryRunner.execute(_conn, sql, null, null);
	}

	public void execute(String sql, Class<? extends IAggregateRoot> forkAggregateRootType) {
		QueryRunner.execute(_conn, sql, forkAggregateRootType.getName());
	}

	public void execute(String sql, String forkAggregate) {
		QueryRunner.execute(_conn, sql, forkAggregate);
	}

	/**
	 * 
	 * 执行sql，并且不复刻指令到其他服务器
	 * 
	 * @param sql
	 */
	public int execute(String sql, MapData param) {
		return QueryRunner.execute(_conn, sql, param, null);
	}

	public int execute(String sql, MapData param, Class<? extends IAggregateRoot> forkAggregateRootType) {
		return QueryRunner.execute(_conn, sql, param, forkAggregateRootType.getName());
	}

	public int execute(String sql, MapData param, String forkAggregate) {
		return QueryRunner.execute(_conn, sql, param, forkAggregate);
	}

	public Object queryScalar(String sql, MapData params) {
		return QueryRunner.queryScalar(_conn, sql, params);
	}

	public Object queryScalar(String sql, MapData params, QueryLevel level) {
		sql = supplementLock(sql, level);
		return QueryRunner.queryScalar(_conn, sql, params);
	}

	public <T> T queryScalar(Class<T> valueType, String sql, MapData param, QueryLevel level) {
		sql = supplementLock(sql, level);
		return QueryRunner.queryScalar(valueType, _conn, sql, param);
	}

	public int queryScalarInt(String sql, MapData params) {
		return QueryRunner.queryScalarInt(_conn, sql, params);
	}

	public int queryScalarInt(String sql, MapData params, QueryLevel level) {
		sql = supplementLock(sql, level);
		return QueryRunner.queryScalarInt(_conn, sql, params);
	}

	public long queryScalarLong(String sql, MapData params, QueryLevel level) {
		sql = supplementLock(sql, level);
		return QueryRunner.queryScalarLong(_conn, sql, params);
	}

	public long queryScalarLong(String sql) {
		return QueryRunner.queryScalarLong(_conn, sql, null);
	}

	public UUID queryScalarGuid(String sql, MapData params, QueryLevel level) {
		sql = supplementLock(sql, level);
		return QueryRunner.queryScalarGuid(_conn, sql, params);
	}

	public Iterable<Object> queryScalars(String sql, MapData params, QueryLevel level) {
		sql = supplementLock(sql, level);
		return QueryRunner.queryScalars(_conn, sql, params);
	}

	public <T> Iterable<T> queryScalars(Class<T> elementType, String sql, MapData params, QueryLevel level) {
		sql = supplementLock(sql, level);
		return QueryRunner.queryScalars(elementType, _conn, sql, params);
	}

	public <T> Iterable<T> queryScalars(Class<T> elementType, String sql, MapData params) {
		return QueryRunner.queryScalars(elementType, _conn, sql, params);
	}

	public int[] queryScalarInts(String sql, MapData params, QueryLevel level) {
		sql = supplementLock(sql, level);
		return QueryRunner.queryScalarInts(_conn, sql, params);
	}

	public DTObject queryDTO(String sql, MapData params, QueryLevel level) {
		sql = supplementLock(sql, level);
		return QueryRunner.queryDTO(_conn, sql, params);
	}

	public Iterable<DTObject> queryDTOs(String sql, MapData params, QueryLevel level) {
		sql = supplementLock(sql, level);
		return QueryRunner.queryDTOs(_conn, sql, params);
	}

	public MapData queryRow(String sql, MapData params, QueryLevel level) {
		sql = supplementLock(sql, level);
		return QueryRunner.queryRow(_conn, sql, params);
	}

	public MapData queryRow(String sql, MapData params) {
		return QueryRunner.queryRow(_conn, sql, params);
	}

	public Iterable<MapData> queryRows(String sql, MapData params) {
		return QueryRunner.queryRows(_conn, sql, params);
	}

	public Iterable<MapData> queryRows(String sql, MapData params, QueryLevel level) {
		sql = supplementLock(sql, level);
		return QueryRunner.queryRows(_conn, sql, params);
	}

	private static String supplementLock(String sql, QueryLevel level) {
		return SqlStatement.supplementLock(sql, level);
	}

	public static DataAccess getCurrent() {
		if (!DataContext.existCurrent())
			return null;
		return DataContext.getCurrent().connection().access();
	}

}
