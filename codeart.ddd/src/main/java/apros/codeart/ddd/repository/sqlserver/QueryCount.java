package apros.codeart.ddd.repository.sqlserver;

import apros.codeart.ddd.EntityObject;
import apros.codeart.ddd.QueryLevel;
import apros.codeart.ddd.repository.access.DataTable;
import apros.codeart.ddd.repository.access.QueryCountQB;
import apros.codeart.ddd.repository.access.internal.SqlDefinition;
import apros.codeart.ddd.repository.access.internal.SqlStatement;
import apros.codeart.ddd.repository.db.DBUtil;
import apros.codeart.ddd.repository.sqlserver.ExpressionHelper;
import apros.codeart.util.SafeAccess;

@SafeAccess
class QueryCount extends QueryCountQB {

	private QueryCount() {
	}

	protected String buildImpl(DataTable target, SqlDefinition definition, QueryLevel level) {
		String tableName = target.name()+"CTE";

		String objectSql = ExpressionHelper.getObjectSql(target,level, definition);

		var bottomSql = String.format("select count(DISTINCT %s) from %s",EntityObject.IdPropertyName,
				SqlStatement.qualifier(tableName));

		bottomSql = DBUtil.addQualifier(ExpressionHelper.mapToCCJSqlParser(bottomSql,null),target,tableName);
		bottomSql = ExpressionHelper.mapToSQLServer(bottomSql,null);

		return String.format("%s%s%s",objectSql,System.lineSeparator(),bottomSql);
	}

	public static final QueryCount Instance = new QueryCount();

}
