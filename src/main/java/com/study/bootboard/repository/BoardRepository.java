package com.study.bootboard.repository;

import com.study.bootboard.entity.Board;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardRepository extends JpaRepository<Board, Integer> {
                                            //<어떤엔티티를 넣을거냐,해당엔티티@Id타입>

//(findBy컬럼이름Containing)'한코딩'검색하고싶을때 한만입력해도됨 Containing없으면 같게 입력해야함 키워드랑아예
 Page<Board> findByTitleContaining(String searchKeyword, Pageable pageable);
}
