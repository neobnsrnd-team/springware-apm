package kr.springware.profiler.demo.problem.mapper;

import kr.springware.profiler.demo.problem.model.DemoItem;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DemoMapper {

    List<DemoItem> findAll();

    List<DemoItem> findByCategory(String category);

    List<String> findAllCategories();
}
