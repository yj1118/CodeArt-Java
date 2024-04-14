package com.apros.codeart.ddd.repository.access;

import com.apros.codeart.ddd.QueryLevel;

public abstract class QueryObjectQB implements IQueryBuilder {

	public QueryObjectQB() {

	}

	public String build(QueryDescription description) {
		var target = description.table();
		String expression = description.getItem("expression");
		QueryLevel level = description.getItem("level");
		return _cache.getSql(target, expression, level);
	}

	protected abstract String buildImpl(DataTable target, String expression, QueryLevel level);

	private ExpressionCache<QueryObjectQB> _cache = new ExpressionCache<QueryObjectQB>((target, expression, level) -> {
		return buildImpl(target, expression, level);
	});

}
