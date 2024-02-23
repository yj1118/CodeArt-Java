package apros.codeart.dto;

import java.util.function.Function;

abstract class DTEntity implements AutoCloseable {

	private String _name;

	public String getName() {
		return _name;
	}

	public void setName(String name) {
		_name = name;
	}

	private DTEntity _parent;

	public DTEntity getParent() {
		return _parent;
	}

	public void setParent(DTEntity parent) {
		_parent = parent;
	}

	public DTEntity() {
	}

	/**
	 * dto成员是否含有数据
	 * 
	 * @return
	 */
	public abstract boolean hasData();

	public abstract DTEntityType getType();

	private Iterable<DTEntity> _selfAsEntities;

	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	public Iterable<DTEntity> getSelfAsEntities() throws Exception {
		if (_selfAsEntities == null) {
			var es = obtainList();
			es.add(this);
			_selfAsEntities = es;
		}
		return _selfAsEntities;
	}

	public final Object clone() throws CloneNotSupportedException {
		try {
			return cloneImpl();
		} catch (Exception e) {
			throw new CloneNotSupportedException(e.getMessage());
		}
	}

	protected abstract DTEntity cloneImpl() throws Exception;

	public abstract void setMember(QueryExpression query, Function<String, DTEntity> createEntity) throws Exception;

	/**
	 * 删除成员
	 * 
	 * @param e
	 */
	public abstract void removeMember(DTEntity e);

	/**
	 * 根据查找表达式找出dto成员
	 * 
	 * @param query
	 * @return
	 * @throws Exception
	 */
	public abstract Iterable<DTEntity> finds(QueryExpression query) throws Exception;

	/**
	 * @param sequential 是否排序输出代码
	 * @param outputName 是否输出key值
	 * @return
	 * @throws Exception
	 */
	public abstract String getCode(boolean sequential, boolean outputName) throws Exception;

	/**
	 * 输出架构码
	 * 
	 * @param sequential
	 * @param outputName
	 * @return
	 */
	public abstract String getSchemaCode(boolean sequential, boolean outputName) throws Exception;

	public void close() throws Exception {
		// 接触引用，防止循环引用导致内存泄漏
		_name = null;
		_parent = null;
		_selfAsEntities = null;
	}

	public abstract void clearData();
}
