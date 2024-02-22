package com.study.bootboard.service;

import com.study.bootboard.entity.Board;
import com.study.bootboard.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;

    //글 작성 처리
    public void write(Board board, MultipartFile file) throws IOException {

        if(file.isEmpty()){
            boardRepository.save(board);
            return;
        }
           //현재 프로젝트 경로를 담아줌     +    파일디렉토리   (저장할 파일경로)
           String projectpath = System.getProperty("user.dir") + "/src/main/resources/static/files";
           UUID uuid = UUID.randomUUID();
           String fileName = uuid + "_" + file.getOriginalFilename();//랜덤난수+원래파일명

           File saveFile = new File(projectpath, fileName);//저장될 경로와 파일이름지정(받은 파일을 넣어줄 파일 껍데기인셈)
           file.transferTo(saveFile);

           board.setFilename(fileName);
           board.setFilepath(projectpath + "/" + fileName);

        boardRepository.save(board);
    }

    public void update(Board board, MultipartFile file) throws IOException {
        if(file.isEmpty()){
            boardRepository.save(board);
            return;
        }
        //현재 프로젝트 경로를 담아줌     +    파일디렉토리   (저장할 파일경로)
        String projectpath = System.getProperty("user.dir") + "/src/main/resources/static/files";
        UUID uuid = UUID.randomUUID();
        String fileName = uuid + "_" + file.getOriginalFilename();//랜덤난수+원래파일명

        File saveFile = new File(projectpath, fileName);//저장될 경로와 파일이름지정(받은 파일을 넣어줄 파일 껍데기인셈)
        file.transferTo(saveFile);

        // 기존 파일이 존재하는 경우, 기존 파일 삭제
        if (board.getFilepath() != null && !board.getFilepath().isEmpty()) {
            File existingFile = new File(board.getFilepath());
            if (existingFile.exists()) {
                existingFile.delete(); // 기존 파일 삭제
            }
        }

        board.setFilename(fileName);
        board.setFilepath(projectpath + "/" + fileName);

        boardRepository.save(board);
    }


//    // 게시글 리스트 처리  (내림차순으로 정렬) 매개변수없는 기본값은 오름차순
//    public List<Board> boardList() {
//        return boardRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
//    }

    // 게시글 리스트 처리  <Pageable인터페이스 쓰는경우>
    public Page<Board> boardList(Pageable pageable) {
        return boardRepository.findAll(pageable);
    }

    public Page<Board> boardSearchList(String searchKeyword, Pageable pageable){
        return boardRepository.findByTitleContaining(searchKeyword,pageable);
    }


    //특정 게시글 불러오기
    public Board boardView(Integer id){
        return boardRepository.findById(id).get();//optional로 받아오니 get()추가해주자
    }

    //특정 게시글 삭제
    public void boardDelete(Integer id){
        boardRepository.deleteById(id);
    }

}
