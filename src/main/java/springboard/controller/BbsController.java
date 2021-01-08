package springboard.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import model.JdbcTemplateConst;

@Controller
public class BbsController {
	
	private JdbcTemplate template;

	@Autowired
	public void setTemplate(JdbcTemplate template) {
		this.template = template;
		System.out.println("@Autowired->JDBCTemplate 연결성공");
		JdbcTemplateConst.template = this.template;
	}
	
	@RequestMapping("/board/list.do")
	public String list(Model model, HttpServletRequest req) {
		
		model.addAttribute("req", req);
		
		return "07Board/list";
	}
	
	
}
