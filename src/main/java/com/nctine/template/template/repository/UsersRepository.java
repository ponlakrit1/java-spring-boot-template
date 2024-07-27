package com.nctine.template.template.repository;

import com.nctine.template.template.entity.UsersEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersRepository extends CrudRepository<UsersEntity, Long> {

    UsersEntity findByUsernameAndActiveIsTrue(String username);

}
