package repository;

import apros.codeart.ddd.QueryLevel;
import apros.codeart.ddd.launcher.TestLauncher;
import apros.codeart.ddd.metadata.internal.ObjectMetaLoader;
import apros.codeart.ddd.repository.DataContext;
import apros.codeart.ddd.repository.Repository;
import apros.codeart.ddd.repository.TransactionStatus;
import apros.codeart.ddd.repository.access.DataPortal;
import apros.codeart.util.WrapperBoolean;
import apros.codeart.util.concurrent.LatchSignal;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import subsystem.account.Account;
import subsystem.account.AuthPlatform;
import subsystem.account.IAccountRepository;
import subsystem.account.IAuthPlatformRepository;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 我们提供对单元测试里实现的锁关系承诺
 *
 */
public class QueryObjectTest {

    @BeforeAll
    public static void setup() {
        TestLauncher.start();
        initData();
    }

    private static void initData() {
        DataContext.using(() -> {
            create("系统控制", "admin");
            create("用户博客", "blog");
            createAccount("小李","127.0.0.1");
            createAccount("小张","127.0.0.2");
            createAccount("小明","127.0.0.2");
        });
    }

    private static void create(String name, String en) {
        var id = DataPortal.getIdentity(AuthPlatform.class);
        var platform = new AuthPlatform(id, name, en);
        Repository.add(platform);
    }

    private static void createAccount(String name,String ip) {
        var id = DataPortal.getIdentity(Account.class);
        var account = new Account(id,name,"111111");
        account.login(ip);
        Repository.add(account);
    }

    @AfterAll
    public static void clean() {
        TestLauncher.stop();
    }

    @Test
    void query_by_id() {
        long id = DataContext.using(() -> {
            IAuthPlatformRepository repository = Repository.create(IAuthPlatformRepository.class);
            var obj = repository.findByEN("admin",QueryLevel.NONE);
            return obj.id();
        });

        DataContext.using(() -> {
            var obj = Repository.find(AuthPlatform.class,id,QueryLevel.NONE);
            assertAdmin(obj);
        });

    }

    @Test
    void query_by_like_name() {
        DataContext.using(() -> {
            IAuthPlatformRepository repository = Repository.create(IAuthPlatformRepository.class);
            var obj = repository.findByName("统控",QueryLevel.NONE);
            assertAdmin(obj);
        });
    }

    private static void assertAdmin(AuthPlatform obj){
        assertEquals("admin",obj.en());
        assertEquals("系统控制",obj.name());
    }

    private static void assertAccountXL(Account obj){
        assertEquals("小李",obj.name());
        assertEquals("127.0.0.1",obj.status().loginInfo().lastIP());
    }


    @Test
    void query_by_status_isEnabled() {
        DataContext.using(() -> {
            IAccountRepository repository = Repository.create(IAccountRepository.class);
            var obj = repository.findByIsEnabled(true);
            assertAccountXL(obj);
        });
    }

    @Test
    void query_by_status_loginInfo_ip() {
        DataContext.using(() -> {
            IAccountRepository repository = Repository.create(IAccountRepository.class);
            var obj = repository.findByIp("127.0.0.1");
            assertAccountXL(obj);
        });
    }


}