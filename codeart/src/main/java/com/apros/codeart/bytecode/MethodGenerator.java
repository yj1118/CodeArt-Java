package com.apros.codeart.bytecode;

import static com.apros.codeart.i18n.Language.strings;
import static com.apros.codeart.runtime.Util.propagate;

import java.lang.reflect.Method;
import java.util.function.Supplier;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import com.apros.codeart.runtime.DynamicUtil;
import com.google.common.collect.Iterables;

public class MethodGenerator implements AutoCloseable {

	private boolean _isStatic;

	public boolean isStatic() {
		return _isStatic;
	}

	private Iterable<MethodParameter> _prms;

	private MethodVisitor _visitor;

	MethodVisitor visitor() {
		return _visitor;
	}

	private Class<?> _returnClass;

	/**
	 * 局部变量的集合,该集合存放了所有声明过的局部变量
	 */
	private VariableCollection _locals = null;

	VariableCollection locals() {
		return _locals;
	}

	/**
	 * 调用栈
	 */
	private EvaluationStack _evalStack;

	public EvaluationStack evalStack() {
		return _evalStack;
	}

	private ScopeStack _scopeStack;

	public ScopeStack scopeStack() {
		return _scopeStack;
	}

	MethodGenerator(MethodVisitor visitor, int access, Class<?> returnClass, Iterable<MethodParameter> prms) {
		_visitor = visitor;
		_isStatic = (access & Opcodes.ACC_STATIC) != 0;
		_returnClass = returnClass;
		init(prms);
	}

	private void init(Iterable<MethodParameter> prms) {
		_prms = prms;
		_locals = new VariableCollection(this);
		_evalStack = new EvaluationStack();
		_scopeStack = new ScopeStack(this, prms);
		_visitor.visitCode();
	}

	public void loadThis() {
		if (_isStatic)
			throw new IllegalArgumentException(strings("CannotInvokeStatic"));
		_visitor.visitVarInsn(Opcodes.ALOAD, 0);
	}

	/**
	 * 加载参数
	 * 
	 * @param index
	 */
	public void loadParameter(String name) {
		loadVariable(name); // 加载参数就是加载变量
	}

	public void loadParameter(int prmIndex) {
		var prm = Iterables.get(_prms, prmIndex);
		loadVariable(prm.getName()); // 加载参数就是加载变量
	}

	/**
	 * 加载变量
	 * 
	 * @param index
	 */
	public void loadVariable(String name) {
		var local = _scopeStack.getVar(name);
		local.load();
	}

	public void load(int value) {
		_visitor.visitLdcInsn(value);
		_evalStack.push(int.class);
	}

	/**
	 * 将 null 引用推送到操作数栈上
	 */
	public void loadNull() {
		_visitor.visitInsn(Opcodes.ACONST_NULL);
		_evalStack.push(Object.class);// 同步自身堆栈数据
	}

	public void load(String value) {
		if (value == null) {
			loadNull();
			return;
		}

		_visitor.visitLdcInsn(value);
		_evalStack.push(String.class);
	}

	/**
	 * 声明变量
	 * 
	 * @param name
	 * @param type
	 */
	public Variable declare(Class<?> type, String name) {

		var local = _scopeStack.declare(type, name);

		var descriptor = DynamicUtil.getDescriptor(local.getType());
		_visitor.visitLocalVariable(name, descriptor, null, null, null, local.getIndex());

		return local;
	}

	public MethodGenerator loadField(String express) {
		String[] temp = express.split("\\.");
		return loadField(temp[0], temp[1]);
	}

	/**
	 * 加载变量上的字段的值
	 * 
	 * @param varName
	 * @param fieldName
	 */
	public MethodGenerator loadField(String varName, String fieldName) {

		try {
			// 先加载变量
			this.loadVariable(varName);

			var local = _scopeStack.getVar(varName);
			Class<?> objectType = local.getType();
			var field = objectType.getDeclaredField(fieldName);

			Class<?> fieldType = field.getType();
			String typeDescriptor = Type.getDescriptor(fieldType); // 类似："Ljava/lang/String;"
			String owner = Type.getInternalName(objectType);
			_visitor.visitFieldInsn(Opcodes.GETFIELD, owner, field.getName(), typeDescriptor);

			_evalStack.pop(); // 执行完毕后，变量就被弹出了
			_evalStack.push(fieldType);// 值进来了

		} catch (Exception ex) {
			throw propagate(ex);
		}
		return this;
	}

	/**
	 * 执行实例方法
	 * 
	 * @param express
	 * @return
	 */
	public MethodGenerator invoke(String express, Runnable loadParameters) {
		String[] temp = express.split("\\.");
		return invoke(temp[0], temp[1], loadParameters);
	}

	public MethodGenerator invoke(String express) {
		return invoke(express, null);
	}

	/**
	 * 执行实例方法
	 * 
	 * @param varName
	 * @param methodName
	 * @return
	 */
	private MethodGenerator invoke(String varName, String methodName, Runnable loadParameters) {

		try {

			_evalStack.enterFrame(); // 新建立栈帧
			this.loadVariable(varName); // 先加载变量自身，作为实例方法的第一个参数（this）
			var info = _scopeStack.getVar(varName);

			// 加载参数
			if (loadParameters != null)
				loadParameters.run();

			var cls = info.getType();

			var isInterface = cls.isInterface();
			var opcode = isInterface ? Opcodes.INVOKEINTERFACE : Opcodes.INVOKEVIRTUAL;

			var argCount = _evalStack.size() - 1;
			var argClasses = new Class<?>[argCount];

			var pointer = argCount - 1;
			// 弹出栈，并且收集参数
			while (_evalStack.size() > 0) {
				var item = _evalStack.pop();
				if (_evalStack.size() == 0)
					break; // 不收集最后一个，因为这是对象自身，不是传递的参数，不能作为方法的参数查找
				argClasses[pointer] = item.getValueType();
				pointer--;
			}

			Method method = cls.getMethod(methodName, argClasses);

			var descriptor = DynamicUtil.getMethodDescriptor(method);
			var owner = DynamicUtil.getInternalName(info.getType()); // info.getType().getName()

			_visitor.visitMethodInsn(opcode, owner, methodName, descriptor, isInterface);

			_evalStack.exitFrame(); // 调用完毕，离开栈帧

			var returnType = method.getReturnType();
			if (returnType != void.class) {
				_evalStack.push(returnType); // 返回值会给与父级栈
			}

		} catch (Exception ex) {
			throw propagate(ex);
		}
		return this;
	}

	public void when(Supplier<LogicOperator> condition, Runnable trueAction, Runnable falseAction) {

		var op = condition.get();
		var trueStartLabel = new Label();
		op.run(this, trueStartLabel);

		// 先执行falseAction

		this._scopeStack.using(falseAction);
		var endLabel = new Label();

		_visitor.visitJumpInsn(Opcodes.GOTO, endLabel);
		_visitor.visitLabel(trueStartLabel);
		this._scopeStack.using(trueAction);

		_visitor.visitLabel(endLabel);
	}

	public void when(Supplier<LogicOperator> condition, Runnable trueAction) {
		var op = condition.get();
		var trueStartLabel = new Label();
		op.run(this, trueStartLabel);
		var endLabel = new Label();
		_visitor.visitJumpInsn(Opcodes.GOTO, endLabel);
		_visitor.visitLabel(trueStartLabel);
		this._scopeStack.using(trueAction);

		_visitor.visitLabel(endLabel);
	}

	public void exit() {
		var size = _evalStack.size();
		if (size == 0) {
			if (_returnClass != void.class)
				throw new IllegalArgumentException(strings("ReturnTypeMismatch"));
			_visitor.visitInsn(Opcodes.RETURN);
			return;
		}

		if (size > 1) {
			throw new IllegalArgumentException(strings("ReturnError"));
		}
		var lastType = _evalStack.pop().getValueType(); // 返回就是弹出栈顶得值，给调用方用

		if (lastType != _returnClass) {
			throw new IllegalArgumentException(strings("ReturnTypeMismatch"));
		}

		if (!lastType.isPrimitive()) {
			_visitor.visitInsn(Opcodes.ARETURN);
			return;
		}
		if (lastType == int.class) {
			_visitor.visitInsn(Opcodes.IRETURN);
			return;
		}
		if (lastType == long.class) {
			_visitor.visitInsn(Opcodes.LRETURN);
			return;
		}
		if (lastType == float.class) {
			_visitor.visitInsn(Opcodes.FRETURN);
			return;
		}
		if (lastType == double.class) {
			_visitor.visitInsn(Opcodes.DRETURN);
			return;
		}
		throw new IllegalArgumentException(strings("UnknownException"));
	}

	public void close() {
		exit();
		// 由于开启了COMPUTE_FRAMES ，所以只用调用visitMaxs即可，不必设置具体的值
		_visitor.visitMaxs(0, 0);
		_visitor.visitEnd();

		_scopeStack = null;
		_evalStack = null;
		_visitor = null;
		_locals = null;
	}
}
