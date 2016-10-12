package cn.wcl.test.mogodb.dao.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import cn.wcl.test.mogodb.bean.Person;
import cn.wcl.test.mogodb.dao.MongoTestDao;

public class MongoTestDaoImpl implements MongoTestDao {
	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public void addPerson(Person p) {
		mongoTemplate.insert(p);
	}

	@Override
	public List<Person> getPerson(String id) {
		return mongoTemplate.find(new Query(Criteria.where("id").is(id)), Person.class);
	}

	@Override
	public void delPerson(Person p) {
		mongoTemplate.remove(p);
	}

}
