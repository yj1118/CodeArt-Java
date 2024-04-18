package com.apros.codeart.ddd.remotable;

import com.apros.codeart.ddd.QueryLevel;
import com.apros.codeart.ddd.dynamic.DynamicRoot;
import com.apros.codeart.ddd.dynamic.IDynamicRepository;
import com.apros.codeart.ddd.repository.Repository;

public final class RemotePortal {

	private RemotePortal() {
	}

//	region 获取和同步对象

	/**
	 * 
	 * 获取远程对象并根据配置保存到本地
	 * 
	 * @param <T>
	 * @param objectType 远程对象的类型
	 * @param id 远程对象的编号
	 * @param level
	 * @return
	 */
	static <T extends DynamicRoot> T getObject(Class<T> objectType, Object id, QueryLevel level)
	{
	    var repository = Repository.create(IDynamicRepository.class);
	    var root = repository.find(objectType, id, level);
	    if (!root.isEmpty()) return root;

	    //从远程获取聚合根对象
	    var remoteRoot = getRootByRemote(objectType, id);

	    //保存到本地
	    DataContext.UseTransactionScope(() =>
	    {
	        root = repository.Find(define, id, QueryLevel.HoldSingle);
	        //System.Threading.Thread.Sleep(5000);  取消注释就可以测试paper提交引起的死锁问题，该问题目前已解决
	        if (root.IsEmpty())
	        {
	            root = remoteRoot;
	            AddRoot(repository, define, root);
	        }
	    });
	    return root;
	}

	/**
	 * 
	 * 从远程加载数据
	 * 
	 * @param objectType
	 * @param id
	 * @return
	 */
	private static <T extends DynamicRoot> T getRootByRemote(Class<T> objectType, Object id) {
		var data = RemoteService.GetObject(define, id);
		return (DynamicRoot) define.CreateInstance(data);
	}

	/// <summary>
	/// 新增对象到本地仓储
	/// </summary>
	/// <param name="repository"></param>
	/// <param name="define"></param>
	/// <param name="root"></param>
	private static void AddRoot(IDynamicRepository repository, AggregateRootDefine define, DynamicRoot root) {
		repository.Add(define, root);
		AddMemberRoots(repository, define, root);
	}

	private static void AddMemberRoots(IDynamicRepository repository, AggregateRootDefine define, DynamicRoot root)
	{
	    var memberRoots = root.GetRoots();
	    foreach (var member in memberRoots)
	    {
	        var id = member.GetIdentity();
	        //为了防止死锁，我们开启的是不带锁的模式判定是否有本地数据
	        //虽然这有可能造成重复输入的插入而导致报错，但是几率非常低，而且不会引起灾难性bug
	        var local = repository.Find(define, id, QueryLevel.None);
	        if (local.IsEmpty())
	        {
	            repository.Add(define, member);
	        }
	    }
	}

	/// <summary>
	/// 修改远程对象在本地的映射
	/// </summary>
	/// <param name="define"></param>
	/// <param name="id"></param>
	internal

	static void UpdateObject(AggregateRootDefine define, object id)
	{
	    //这里需要注意，我们不能简单的删除本地对象再等待下次访问时加载
	    //因为对象缓存的原因，对象的属性可能引用了已删除的本地对象
	    //这些被引用的以删除的本地对象只有在对象缓存过期后才更新，导致数据更新不及时
	    //因此需要手工更改对象的内容
	    var repository = Repository.Create<IDynamicRepository>();

	    DataContext.UseTransactionScope(() =>
	    {
	        var local = repository.Find(define, id, QueryLevel.HoldSingle);
	        if (local.IsEmpty()) return; //本地没有数据，不需要更新

	        var root = GetRootByRemote(define, id);
	        if (root.IsEmpty())
	        {
	            DeleteObject(define, id);
	            return;
	        }
	        local.Sync(root); //同步数据
	        UpdateRoot(repository, define, local);
	    });
	}

	private static void UpdateRoot(IDynamicRepository repository, AggregateRootDefine define, DynamicRoot root) {
		repository.Update(define, root); // 保存修改后的数据
		AddMemberRoots(repository, define, root); // 有可能修改后的数据包含新的根成员需要增加
	}

	/// <summary>
	/// 删除远程对象在本地的映射
	/// </summary>
	/// <param name="define"></param>
	/// <param name="id"></param>
	internal

	static void DeleteObject(AggregateRootDefine define, object id)
	{
	    var repository = Repository.Create<IDynamicRepository>();
	    var root = repository.Find(define, id, QueryLevel.Single);
	    if (!root.IsEmpty())
	    {
	        if (define.KeepSnapshot)
	        {
	            //保留快照
	            root.MarkSnapshot();
	            repository.Update(define, root);
	        }
	        else
	        {
	            //同步删除
	            repository.Delete(define, root);
	        }
	        
	    }
	}

	#endregion

	#

	region 广播消息

	/// <summary>
	/// 通知对象已修改
	/// </summary>
	/// <param name="type"></param>
	/// <param name="id"></param>
	internal

	static void NotifyUpdated(RemoteType type, object id) {
		RemoteService.NotifyUpdated(type, id);
	}

	/// <summary>
	/// 通知对象已删除
	/// </summary>
	/// <param name="type"></param>
	/// <param name="id"></param>
	internal

	static void NotifyDeleted(RemoteType type, object id) {
		RemoteService.NotifyDeleted(type, id);
	}

	#endregion

}
