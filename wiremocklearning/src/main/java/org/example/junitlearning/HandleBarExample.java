package org.example.junitlearning;

import com.github.jknack.handlebars.Handlebars;

import java.io.IOException;
import java.util.HashMap;

public class HandleBarExample {

    private static final String responseBody = """
                <pets>
                     <pet>
                         <id>{{id}}</id>
                         <name>{{name}}</name>
                         <category>
                             <id>{{category.id}}</id>
                             <name>{{category.name}}</name>
                         </category>
                         <status>sold</status>
                         <photoUrls>
                             <photoUrls>http://localhost:8080/pic.jpg</photoUrls>
                         </photoUrls>
                         <tags>
                             <tags>
                                 <id>1</id>
                                 <name>Good Dog</name>
                             </tags>
                         </tags>
                     </pet>
                 </pets>
            """.trim();

    public static void main(String[] args) throws IOException {
        var handleBar = new Handlebars();
        var template = handleBar.compileInline(responseBody);

        var category = new HashMap<String ,String>();
        category.put("id", "78");
        category.put("name", "Cat");

        var model = new HashMap<String, Object>();
        model.put("id", "90");
        model.put("name", "Bruno Nine");
        model.put("category",category);

        var output = template.apply(model);
        System.out.println(output);

    }
}
