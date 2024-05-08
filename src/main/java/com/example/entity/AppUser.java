package com.example.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
public class AppUser {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long userId;

	@Email(message = "{appuser.email.not.valid}")
	@NotBlank(message = "{appuser.email.not.empty}")
	private String email;

	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private String password;

	@NotBlank(message = "{appuser.fname.not.empty}")
	private String firstName;

	@NotBlank(message = "{appuser.lname.not.empty}")
	private String lastName;

	@ManyToMany(cascade = CascadeType.ALL, targetEntity = Role.class)
	@JoinTable(name = "APP_USER_ROLE_MAPPING")
	private Set<Role> roles;
}