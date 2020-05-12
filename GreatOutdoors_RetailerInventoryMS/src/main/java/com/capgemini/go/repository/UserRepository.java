package com.capgemini.go.repository;

import org.springframework.data.repository.CrudRepository;

import com.capgemini.go.dto.UserDTO;

public interface UserRepository extends CrudRepository<UserDTO, String> {

}
