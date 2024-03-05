package com.apros.codeart.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.apros.codeart.TestRunner;
import com.google.common.collect.Iterables;

@ExtendWith(TestRunner.class)
class CommonTest {

	@Test
	public void createDTO() {
		DTObject dto = DTObject.editable("{id,name}");
		dto.setInt("id", 1);
		dto.setString("name", "刘备");

		assertEquals(1, dto.getInt("id"));
		assertEquals("刘备", dto.getString("name"));
		assertEquals("{\"id\":1,\"name\":\"刘备\"}", dto.getCode());
		assertEquals("{id,name}", dto.getSchemaCode());
	}

	@Test
	public void createHaveValueDTO() {
		DTObject dto = DTObject.readonly("{id:1,name:\"Louis\"}");

		assertEquals(1, dto.getInt("id"));
		assertEquals("Louis", dto.getString("name"));
		assertEquals("{\"id\":1,\"name\":\"Louis\"}", dto.getCode());
		assertEquals("{id,name}", dto.getSchemaCode());
	}

	@Test
	public void createListDTO() {
		var dto = DTObject.editable("{id,name,hobby:[{v,n}]}");
		dto.setInt("id", 1);
		dto.setString("name", "Louis");
		DTObject obj = dto.push("hobby");
		obj.setInt("v", 0);
		obj.setString("n", String.format("LS%s", 0));

		obj = dto.push("hobby");
		obj.setInt("v", 1);
		obj.setString("n", String.format("LS%s", 1));

		var list = dto.getList("hobby");
		for (int i = 0; i < Iterables.size(list); i++) {
			var t = Iterables.get(list, i);
			assertEquals(i, t.getInt("v"));
			assertEquals(String.format("LS%s", i), t.getString("n"));
		}

//        Assert.AreEqual(1, dto.GetValue<int>("id"));
//        Assert.AreEqual("Louis", dto.GetValue<string>("name"));
		// Assert.AreEqual("{\"id\",\"name\",\"hobby\":[{\"v\",\"n\"}]}",
		// dto.GetCode(false));
		// Assert.AreEqual("{\"id\":1,\"name\":\"Louis\",\"hobby\":[{\"v\":0,\"n\":\"LS0\"},{\"v\":1,\"n\":\"LS1\"}]}",
		// dto.GetCode());

//        var code = dto.GetCode();
//        var copy = DTObject.Create(code);
//        list = dto.GetList("hobby");
//        for (int i = 0; i < list.Count; i++)
//        {
//            Assert.AreEqual(i, list[i].GetValue<int>("v"));
//            Assert.AreEqual(string.Format("LS{0}", i), list[i].GetValue<string>("n"));
//        }
//
//        Assert.AreEqual(1, dto.GetValue<int>("id"));
//        Assert.AreEqual("Louis", dto.GetValue<string>("name"));

	}

//
//    [TestMethod]
//    public void CreateNestListDTO()
//    {
//        DTObject dto = DTObject.Create("{items:[{v,n,childs:[{v,n}]}]}");
//
//        DTObject objItems = dto.CreateAndPush("items");
//        objItems.SetValue("v", 0);
//        objItems.SetValue("n", string.Format("item{0}", 0));
//
//        objItems = dto.CreateAndPush("items");
//        objItems.SetValue("v", 1);
//        objItems.SetValue("n", string.Format("item{0}", 1));
//
//        DTObject objChilds = objItems.CreateAndPush("childs");
//        objChilds.SetValue("v", 10);
//        objChilds.SetValue("n", string.Format("child{0}", 10));
//
//        objChilds = objItems.CreateAndPush("childs");
//        objChilds.SetValue("v", 20);
//        objChilds.SetValue("n", string.Format("child{0}", 20));
//
//
//        //Assert.AreEqual("{\"items\":[{\"v\",\"n\",\"childs\":[{\"v\",\"n\"}]}]}", dto.GetCode(false));
//        Assert.AreEqual("{\"items\":[{\"childs\":[],\"n\":\"item0\",\"v\":0},{\"childs\":[{\"n\":\"child10\",\"v\":10},{\"n\":\"child20\",\"v\":20}],\"n\":\"item1\",\"v\":1}]}", dto.GetCode(true));
//    }
//
//    [TestMethod]
//    public void CreateSymbolDTO()
//    {
//        DTObject dto = DTObject.Create("{id,name,sex,hobbys:[{v,n}]}");
//        dto.SetValue("id", 1);
//        dto.SetValue("name", "loui's");
//        dto.SetValue("sex", 9);
//
//        DTObject objHobbys = dto.CreateAndPush("hobbys");
//        objHobbys.SetValue("v", "1");
//        objHobbys.SetValue("n", "！@#09/");
//
//        Assert.AreEqual(1, dto.GetValue<int>("id"));
//        Assert.AreEqual("loui's", dto.GetValue<string>("name"));
//        Assert.AreEqual(9, dto.GetValue<int>("sex"));
//        //Assert.AreEqual("{\"id\",\"name\",\"sex\",\"hobbys\":[{\"v\",\"n\"}]}", dto.GetCode(false));
//        Assert.AreEqual("{\"hobbys\":[{\"n\":\"！@#09/\",\"v\":\"1\"}],\"id\":1,\"name\":\"loui's\",\"sex\":9}", dto.GetCode(true));
//    }
//
//    [TestMethod]
//    public void CreateGuidDTO()
//    {
//        DTObject dto = DTObject.Create("{id}");
//        dto.SetValue("id", Guid.Empty);
//
//        Assert.AreEqual(Guid.Empty, dto.GetValue<Guid>("id"));
//    }
//
//    [TestMethod]
//    public void CreateStringDTO()
//    {
//        DTObject dto = DTObject.Create("{name}");
//        dto.SetValue("name", string.Empty);
//
//        Assert.AreEqual(string.Empty, dto.GetValue<string>("name"));
//    }
//
//    [TestMethod]
//    public void CreateBoolDTO()
//    {
//        DTObject dto = DTObject.Create("{isShow}");
//        dto.SetValue("isShow", true);
//
//        Assert.AreEqual(true, dto.GetValue<bool>("isShow"));
//    }
//
//    [TestMethod]
//    public void CreateDateTimeDTO()
//    {
//        DTObject dto = DTObject.Create("{time}");
//        dto.SetValue("time", DateTime.Parse("2031-08-05"));
//
//        Assert.AreEqual(DateTime.Parse("2031-08-05"), dto.GetValue<DateTime>("time"));
//    }
//
//    [TestMethod]
//    public void CreateObjectDTO()
//    {
//        var user = new User(1, "Louis");
//        DTObject dto = DTObject.Create("{user}");
//        var dtoUser = DTObject.Create("{id,name}", user);
//        dto.SetValue("user", dtoUser);
//
//        dynamic result = dto.GetValue("user");
//
//        Assert.AreEqual(1, result.Id);
//        Assert.AreEqual("Louis", result.Name);
//    }
//
//    private class User
//    {
//        private int _id;
//
//        public int Id
//        {
//            get { return _id; }
//            set { _id = value; }
//        }
//
//        private string _name;
//
//        public string Name
//        {
//            get { return _name; }
//            set { _name = value; }
//        }
//
//        public User(int id, string name) { _id = id; _name = name; }
//    }
//
//
//

	@Test
	public void createSocketMessageDTO() {
		DTObject dto = DTObject.readonly(
				"{\"RCN\":\"ControlBigScreenCapability\",\"REN\":\"PlayEvent\",\"MT\":7,\"Ds\":[\"[::ffff:192.168.0.13]:59714\"]}");
		var ds = dto.getList("Ds");

		assertEquals(1, ds.size());

		assertEquals("[::ffff:192.168.0.13]:59714", ds.get(0).getString());
	}

}
