package com.mb.AsistenteVirtual.Controladores;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
*
* @author Marcos Baró
*/

@Controller
public class AsistenteVirtualControlador {
	
	private final ChatClient chatClient;
	
	private final ChatModel chatModel;
	
	public AsistenteVirtualControlador(ChatClient.Builder builder, ChatModel chatModel) {

		this.chatClient = builder
//				.defaultSystem("Dame una explicación sencilla y fácil de entender, como si fueras a explicárselo a un estudiante de secundaria.")
				.build();

		this.chatModel = chatModel;

	}
	
	@PostMapping("/procesar")
	public ModelAndView procesarPregunta(@RequestParam String pregunta, 
			@RequestParam String dificultad,
			ModelAndView mv) {

		String respuesta = "";
				
		String promptAdicional = "";
		
		if (dificultad.equals("Facil")) {
			
			promptAdicional = "Dame una explicación sencilla y fácil de entender, como si fueras a explicárselo a un estudiante de secundaria.";
		
		} else {
			
			promptAdicional = "Explica esto en detalle, cubriendo todos los aspectos, incluyendo ejemplos y profundizando en los conceptos.";
		
		}
		
		String consultaAdaptada = pregunta + " " + promptAdicional;
		
		try {
			
			respuesta = chatClient
					.prompt()						// Construir un "prompt" (entrada) para enviar al modelo
		            .user(consultaAdaptada)			// Pasamos el mensaje del usuario
		            .options(ChatOptions.builder()
		            .temperature(0.4)   			// Ajuste de la creatividad del modelo (0.0 - 1.0). Un valor bajo (como 0.4) hace que la respuesta sea mas coherente y determinista y con un valor mas alto (por ejemplo, 0.8), la respuesta puede ser mas creativa o diversa, pero tambien mas impredecible
		            //.maxTokens(150)     			// Limita la cantidad de tokens en la respuesta
		            .topP(0.9)          			// Nucleus Sampling, mayor diversidad en las respuestas
		            .frequencyPenalty(0.5)  		// Ajusta la probabilidad de que el modelo repita las mismas palabras o frases. Un valor alto reduce la posibilidad de que el modelo repita lo mismo, lo cual puede hacer las respuestas mas diversas
		            .presencePenalty(0.3)   		// Penaliza la aparicion de nuevos terminos o conceptos. Con un valor mas alto, el modelo evitara usar conceptos o palabras que ya han aparecido en el contexto del diálogo
		            .build())
		            .call()							// Llama al modelo
		            .content();						// Obtiene la respuesta
				
//			// Otra forma utilizando un objeto de tipo ChatModel
//			respuesta = chatModel.call(consultaAdaptada);
				
		} catch (Exception e) {
			
			respuesta = "Disculpe, Ocurrio un error al generar la respuesta, intente nuevamente, muchas gracias!";
		        
		}
		
		// Pasamos los datos a la vista
		mv.addObject("pregunta", pregunta);
		mv.addObject("facil", dificultad.equals("Facil") ? "Facil" : null);
		mv.addObject("avanzado", dificultad.equals("Avanzado") ? "Avanzado" : null);
		mv.addObject("respuesta", respuesta);

		// Seteamos el nombre de la vista
		mv.setViewName("index");
		return mv;

	}
	
	@GetMapping("/rag")
	public ModelAndView procesarPreguntaConRag(ModelAndView mv) {

		mv.setViewName("rag");
		
		return mv;
		
	}

}
