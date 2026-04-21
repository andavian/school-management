package org.school.management.students.records.infrastructure.web.mapper;

import org.mapstruct.Mapper;
import org.school.management.students.records.application.dto.request.DocumentTypeRequest.CreateDocumentTypeRequest;
import org.school.management.students.records.application.dto.request.DocumentTypeRequest.UpdateDocumentTypeRequest;
import org.school.management.students.records.application.dto.response.DocumentTypeResponse;
import org.school.management.students.records.infrastructure.web.dto.DocumentTypeWebDto;

@Mapper(componentModel = "spring")
public interface DocumentTypeWebMapper {

    CreateDocumentTypeRequest toApplicationRequest(
            DocumentTypeWebDto.CreateDocumentTypeWebRequest webRequest);

    UpdateDocumentTypeRequest toApplicationRequest(
            DocumentTypeWebDto.UpdateDocumentTypeWebRequest webRequest);

    DocumentTypeWebDto.DocumentTypeWebResponse toWebResponse(DocumentTypeResponse response);
}