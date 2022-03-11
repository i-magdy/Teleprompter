package ibrahimmagdy.example.com.teleprompter.models;

public class ScriptModel  {


    private String title;
    private String script;

    public ScriptModel(){

    }


    public ScriptModel(String title,String script){

        this.title = title;
        this.script = script;
    }



    public String getTitle() {
        return title;
    }

    public String getScript() {
        return script;
    }


    public void setTitle(String title) {
        this.title = title;
    }

    public void setScript(String script) {
        this.script = script;
    }

}
