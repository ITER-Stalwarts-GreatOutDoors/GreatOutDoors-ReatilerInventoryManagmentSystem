package com.capgemini.go.repository;

import org.springframework.data.repository.CrudRepository;

import com.capgemini.go.bean.RetailerInventoryBean;

public interface UserRepository extends CrudRepository<RetailerInventoryBean, String> {

}
