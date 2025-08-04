package org.livin.checklist.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.livin.checklist.dto.ChecklistCreateRequestDTO;
import org.livin.checklist.dto.ChecklistDTO;
import org.livin.checklist.mapper.ChecklistMapper;
import org.livin.config.RootConfig;
import org.livin.config.SecurityConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;


import lombok.extern.log4j.Log4j2;

@ExtendWith(SpringExtension.class)  // JUnit 5 + Spring 연동
@ContextConfiguration(classes = {RootConfig.class, SecurityConfig.class}) // 테스트에서 사용할 빈 설정
@Log4j2
class ChecklistServiceImplTest {

	@Autowired
	private ChecklistMapper mapper;
	@Autowired
	private ChecklistService checklistService;

	@Test
	void createChecklist() {
		ChecklistCreateRequestDTO dto = ChecklistCreateRequestDTO.builder()
			.title("Junit 테스트 체크리스트")
			.description("테스트용 설명입니다.")
			.type("PHYSICAL")
			.build();

		Long userId = 1L; // 테스트용 유저 ID

		ChecklistDTO created = checklistService.createChecklist(dto, userId);

		System.out.println("✅ 생성된 체크리스트 ID: " + created.getChecklistId());
	}
}