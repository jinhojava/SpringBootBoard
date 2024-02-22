package com.study.bootboard.controller;

import com.study.bootboard.entity.Board;
import com.study.bootboard.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

@Controller
@RequiredArgsConstructor
public class BoardController {


    private final BoardService boardService;

    @GetMapping("/")
    public String board(){
        return "index";
    }

    @GetMapping("board/write")
    public String boardWriteForm() {

        return "boardwrite";
    }

    @PostMapping("/board/write")
    public String boardWriterPro(Board board, Model model, MultipartFile file) throws IOException {
        boardService.write(board, file);

        model.addAttribute("message","글 작성 완료!");//message창띄워주기위해
        model.addAttribute("searchUrl", "/board/list");//message창띄워주기위해
        return "message";//message창 띄워주기위해/만약 필요없다면 리다이렉트 리스트페이지로
    }

    @GetMapping("/board/list")       //리스트목록 (페이징처리를 위한 파라미터추가)(페이지번호, 한페이지에서보여줄항목수,정렬기준, 정렬방법)
    public String boardList(Model model,
                            @PageableDefault(page=0, size=10, sort="id",direction = Sort.Direction.DESC)Pageable pageable,
                            String searchKeyword) {
        Page<Board>list = null;

        if(searchKeyword!=null){
            list = boardService.boardSearchList(searchKeyword,pageable);
        }
        else {
            list = boardService.boardList(pageable);
        }
                                                            //Pageable기준으로는 0페이지부터 시작이므로 1더해준다
        int nowPage = list.getPageable().getPageNumber()+1;//현재 페이지에 대한 페이지 정보를 포함한 Pageable 객체가 반환->그 객체의 페이지넘버
        int startPage = Math.max(nowPage-4, 1);//현재 블럭에서 시작페이지, -값나오면 안되니 페이지 최솟값은1로 설정
        int endPage = Math.min(nowPage+5,list.getTotalPages());//현재 블럭에서 끝페이지, 초과되어 없는페이지값나오면 안되니 min사용)

        model.addAttribute("list",list);
        model.addAttribute("nowPage",nowPage);
        model.addAttribute("startPage",startPage);
        model.addAttribute("endPage",endPage);
        return "boardlist";
    }

    @GetMapping("/board/view")//localhost:8080/board/view?id=1    //상세화면
    public String boardView(Model model, @RequestParam Integer id) {
        model.addAttribute("board", boardService.boardView(id));

        return "boardview";
    }
    //실제 이미지 파일 url
    @ResponseBody
    @GetMapping("/images/{id}")
    public Resource downloadImage(@PathVariable("id") Integer id) throws MalformedURLException {
        Board board = boardService.boardView(id);

        return new UrlResource("file:" + board.getFilepath());
        //UrlResource가 파일경로와 이름을 주면 알아서 파일 찾아온다.
    }


    @GetMapping("/board/delete")//삭제
    public String boardDelete(@RequestParam Integer id) {

        //디렉토리 파일삭제
        Board board = boardService.boardView(id);
        File file = new File(board.getFilepath());
        file.delete();

        //데이터베이스 객체 삭제
        boardService.boardDelete(id);
        return "redirect:/board/list";
    }

    @GetMapping("/board/modify/{id}")//수정폼
    public String boardModify(@PathVariable("id") Integer id,
                              Model model) {

        model.addAttribute("board", boardService.boardView(id));

        return "boardmodify";
    }

    @PostMapping("/board/update/{id}")//수정하기
    public String boardUpdate(@PathVariable("id") Integer id, Board board, MultipartFile file) throws IOException {
        Board boardTemp = boardService.boardView(id);
        boardTemp.setTitle(board.getTitle());
        boardTemp.setContent(board.getContent());

        boardService.update(boardTemp, file);//수정된것데이터베이스에반영
        return "redirect:/board/list";
    }
}
