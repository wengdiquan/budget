package com.bjsj.budget.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bjsj.budget.dao.AuthorityDao;
import com.bjsj.budget.model.Authority;
import com.bjsj.budget.service.AuthorityService;

@Service("authorityServiceImpl")
public class AuthorityServiceImpl implements AuthorityService {


	@Autowired
	private AuthorityDao authorityDao;

	@Override
	public List<Authority> getMenuInfo(Map<String, Object> queryMap) {
		return authorityDao.getMenuInfo(queryMap);
	}

}
