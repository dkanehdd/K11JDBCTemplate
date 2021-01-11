package springboard.command;

import java.util.ArrayList;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.ui.Model;

import springboard.model.JDBCTemplateDAO;
import springboard.model.SpringBbsDTO;
import springboard.util.EnvFileReader;
import springboard.util.PagingUtil;


/*
BbsCommandImpl 인터페이스를 구현했으므로 execute()는 반드시
오버라이딩 해야한다. 또한 해당 객체는 부모타입인 BbsCommandImpl로
참조할 수 있다.
 */
public class ListCommand implements BbsCommandImpl{

	@Override
	public void execute(Model model) {

		System.out.println("ListCommand > execute() 호출");
		/*
		컨트롤러에서 인자로 전달한 model객체에는 request객체가 저장되어있다.
		asMap()을 통해 Map컬랙션으로 변환한 후 모든 요청을 가져올수 있다.
		 */
		Map<String, Object> paramMap = model.asMap();
		HttpServletRequest req = (HttpServletRequest)paramMap.get("req");
		
		//DAO객체 생성
		JDBCTemplateDAO dao = new JDBCTemplateDAO();
		//검색어 관련 폼값 처리
		String addQueryString = "";
		String searchColumn = req.getParameter("searchColumn");
		String searchWord = req.getParameter("searchWord");
		if(searchWord!=null) {
			addQueryString = String.format("searchColumn=%s&searchWord=%s", 
					searchColumn, searchWord);
			paramMap.put("Column", searchColumn);
			paramMap.put("Word", searchWord);
		}
		
		int pageSize = Integer.parseInt(
				EnvFileReader.getValue("SpringBbsInit.properties", 
						"springBoard.pageSize"));
		int blockPage = Integer.parseInt(
				EnvFileReader.getValue("SpringBbsInit.properties",
						"springBoard.blockPage"));
		//전체 레코드 수 카운트 하기
		int totalRecordCount = dao.getTotalCount(paramMap);
		
		int totalPage = (int)Math.ceil((double)totalRecordCount/pageSize);		
		
		int nowPage = req.getParameter("nowPage")==null ? 1 : 
			Integer.parseInt(req.getParameter("nowPage"));
		
		int start = (nowPage-1)*pageSize +1;
		int end = nowPage * pageSize;
		
		paramMap.put("start", start);
		paramMap.put("end", end);
		//출력할 리스트 가져오기(페이지처리X)
		ArrayList<SpringBbsDTO> listRows = dao.listPage(paramMap);
		
		//가상번호 계산하여 부여하기
		int virtualNum = 0;
		int countNum = 0;
		
		for(SpringBbsDTO row : listRows) {
			//전체게시물의 갯수에서 하나씩 차감하면서 가상번호 부여
			virtualNum = totalRecordCount -(((nowPage-1)*pageSize)+countNum++);
			//setter를 통해 저장
			row.setVirtualNum(virtualNum);
		}
		//리스트에 출력할 List컬렉션을 model객체에 저장한 후 뷰로 전달한다.
		
		String pagingImg = PagingUtil.pagingImg(totalRecordCount,
				pageSize, blockPage, nowPage, 
				req.getContextPath()+"/board/list.do?"+addQueryString);
		
		model.addAttribute("pagingImg", pagingImg);
		model.addAttribute("totalPage", totalPage); //전체페이지수
		model.addAttribute("nowPage", nowPage); //현재페이지번호
		model.addAttribute("listRows", listRows);
		
		//JDBCTemplate에서는 자원반납을 하지 않는다.
		//dao.close();
	}
}
