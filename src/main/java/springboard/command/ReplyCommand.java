package springboard.command;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.ui.Model;

import springboard.model.JDBCTemplateDAO;
import springboard.model.SpringBbsDTO;

public class ReplyCommand implements BbsCommandImpl{

	@Override
	public void execute(Model model) {
		
		Map<String, Object> map = model.asMap();
		HttpServletRequest req = (HttpServletRequest)map.get("req");
		
		String idx = req.getParameter("idx");
		
		JDBCTemplateDAO dao = new JDBCTemplateDAO();
		
		SpringBbsDTO dto = dao.view(idx);
		
		dto.setTitle("[RE]"+dto.getTitle());
		dto.setContents("\n\r\n\r---[원본글]---\n\r"+dto.getContents());
		
		model.addAttribute("replyRow", dto);
	}
}
