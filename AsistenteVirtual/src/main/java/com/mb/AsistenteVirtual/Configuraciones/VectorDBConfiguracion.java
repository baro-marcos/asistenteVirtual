package com.mb.AsistenteVirtual.Configuraciones;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;

/**
*
* @author Marcos Baró
*/

@Configuration
public class VectorDBConfiguracion {
	
	@Value("vector_db.json")
    private String vectorDb;

    @Value("classpath:/Datos/FAQ_Sonda_Lunar.txt")
    Resource faqResource;

    @Bean
    SimpleVectorStore simpleVectorStore(EmbeddingModel embeddingModel) throws IOException {
    	
        SimpleVectorStore vectorStore = SimpleVectorStore.builder(embeddingModel).build(); //Creamos una base de datos de vectores utilizando un modelo de incrustacion (EmbeddingModel (para crear representaciones vectoriales de textos (embeddings))), aquí se almacenaran los vectores generados
        
        File vectorDbFile = getVectorDbFile();
        
        if (vectorDbFile.exists()) {
        	
            vectorStore.load(vectorDbFile);
            
        } else {
        	
            TextReader reader = new TextReader(faqResource);
            List<Document> documentos = reader.get();
            TextSplitter textSplitter = new TokenTextSplitter(); // Para dividir los documentos en fragmentos o tokens

            List<Document> splitDocumentos = textSplitter.apply(documentos);
            vectorStore.add(splitDocumentos);
            vectorStore.save(vectorDbFile);
            
        }
        
        return vectorStore;
        
    }

    private File getVectorDbFile() {
    	
        Path path = Paths.get("src", "main", "resources", "Datos");
        
        String absolutePath = path.toFile().getAbsolutePath() + "/" + vectorDb;
        
        return new File(absolutePath);
        
    }

}
