package apros.codeart.mq;

/**
 * 远程事件处理器
 */
public interface IEventHandler {
	/**
	 * 处理事件
	 * 
	 * @param eventName
	 * @param data
	 */
	void handle(String eventName, TransferData data);

	EventPriority getPriority();

}
