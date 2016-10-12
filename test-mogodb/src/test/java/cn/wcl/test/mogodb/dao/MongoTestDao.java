package cn.wcl.test.mogodb.dao;

import java.util.List;

import cn.wcl.test.mogodb.bean.Person;

public interface MongoTestDao {

	public void addPerson(Person p);

	public void delPerson(Person p);

	public List<Person> getPerson(String id);
}
