package com.tritonkor.persistence.repository.contract;

import com.tritonkor.persistence.entity.Question;
import com.tritonkor.persistence.entity.Result;
import com.tritonkor.persistence.entity.filter.QuestionFilterDto;
import com.tritonkor.persistence.entity.filter.ResultFilterDto;
import com.tritonkor.persistence.repository.Repository;
import java.util.Set;
import java.util.UUID;

public interface ResultRepository extends Repository<Result> {

    Set<Result> findAllByReportId(UUID reportId);

    Set<Result> findAllByTestId(UUID testId);

    Set<Result> findAllByOwnerId(UUID studentId);

    Set<Result> findAll(int offset, int limit, String sortColumn, boolean ascending,
            ResultFilterDto resultFilterDto);
}
