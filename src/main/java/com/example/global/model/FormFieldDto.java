package com.example.global.model;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class FormFieldDto {

	private String fieldName;
	private List<String> errorMsg;
}
