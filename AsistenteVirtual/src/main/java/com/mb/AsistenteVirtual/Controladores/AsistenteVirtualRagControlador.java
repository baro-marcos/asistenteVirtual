package com.mb.AsistenteVirtual.Controladores;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
*
* @author Marcos Baró
*/

@Controller
public class AsistenteVirtualRagControlador {
	
	private final ChatClient chatClient;
	
	private final ChatModel chatModel;
	
	public AsistenteVirtualRagControlador(ChatClient.Builder builder, VectorStore vectorStore, ChatModel chatModel) {

		this.chatClient = builder
				.defaultAdvisors(new QuestionAnswerAdvisor(vectorStore))
//				.defaultSystem("Dame una explicación sencilla y fácil de entender, como si fueras a explicárselo a un estudiante de secundaria.")
				.build();

		this.chatModel = chatModel;

	}
	
	@PostMapping("/procesarRAG")
	public ModelAndView procesarPregunta(@RequestParam String pregunta, 
			@RequestParam String dificultad,
			ModelAndView mv) {

		String respuesta = "";
				
		String promptAdicional = "";
		
		if (dificultad.equals("Facil")) {
			
			promptAdicional = "Dame una explicación sencilla y fácil.";
		
		} else {
			
			promptAdicional = "Explica esto en detalle.";
		
		}
		
		String consultaAdaptada = pregunta + " " + promptAdicional;
		
		try {
			
			respuesta = chatClient
					.prompt()
		            .user(consultaAdaptada)
		            .call()
		            .content();
		
		} catch (Exception e) {
			
			respuesta = "Disculpe, Ocurrio un error al generar la respuesta, intente nuevamente, muchas gracias!";
		        
		}
		
		// Pasamos los datos a la vista
		mv.addObject("pregunta", pregunta);
		mv.addObject("facil", dificultad.equals("Facil") ? "Facil" : null);
		mv.addObject("avanzado", dificultad.equals("Avanzado") ? "Avanzado" : null);
		mv.addObject("respuesta", respuesta);

		// Seteamos el nombre de la vista
		mv.setViewName("rag");
		return mv;

	}

}
