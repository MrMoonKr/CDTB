package lolpatcher;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Rick
 */
public class ConfigurationTask extends PatchTask{
    Main main;
    String slnversion;
    String new_slnversion;
    String server;
    float percentage = 0;
    String language;
    
    boolean lol_air_client = false;
    boolean lol_air_client_config = false;
    boolean lol_game_client = false;
    boolean lol_game_client_Lang = false;
    boolean league_client = false;
    boolean league_client_Lang = false;
    boolean PURGE_UNWANTED_FILES  = false;
    
    
    public ConfigurationTask(Main main){
        this.main = main;
    }
    
    @Override
    public void patch() throws MalformedURLException, IOException, NoSuchAlgorithmException {
        if(! new File("settings.cfg").exists()){
        	System.out.println("No Settings file found creating default file...");
        	String[] settings = {
        			"lol_air_client",
        			"lol_air_client_config",
        			"lol_game_client",
        			"lol_game_client_Lang",
        			"league_client",
        			"league_client_Lang",
        			"PURGE_UNWANTED_FILES"
        	};          
        	
        	try{
        	    PrintWriter writer = new PrintWriter("settings.cfg", "UTF-8");
        	    writer.println("server=NA");
        	    writer.println("language=en_us");
                for (String setting : settings){
                	writer.println(setting + "=false");
                }
        	    writer.close();
        	} catch (IOException e) {
        		System.out.println(e);
        		System.exit(0);
        	}
            System.out.println("Default file created, please configure...");
            System.out.println("Program will now stop.");
            System.exit(0);
        }else{
            Properties props = new Properties();
            try(FileReader fr = new FileReader("settings.cfg")){
                props.load(fr);
            }
            server = props.getProperty("server");
            language = props.getProperty("language");
            lol_air_client=Boolean.parseBoolean(props.getProperty("lol_air_client"));
    		lol_air_client_config=Boolean.parseBoolean(props.getProperty("lol_air_client_config"));
    		lol_game_client=Boolean.parseBoolean(props.getProperty("lol_game_client"));
    		lol_game_client_Lang=Boolean.parseBoolean(props.getProperty("lol_game_client_Lang"));
    		
    		league_client=Boolean.parseBoolean(props.getProperty("league_client"));
    	    league_client_Lang=Boolean.parseBoolean(props.getProperty("league_client_Lang"));
    	    
    		PURGE_UNWANTED_FILES=Boolean.parseBoolean(props.getProperty("PURGE_UNWANTED_FILES"));
    		
            addPatchers();
        }
    }
    
    private void getSolutionManifest(String version, String branch,String ab) throws IOException{
    	System.out.println("RADS/solutions/"+ab+"/releases/" + version + "/solutionmanifest");
        URL u = new URL("http://l3cdn.riotgames.com/releases/"+branch+"/solutions/"+ab+"/releases/"+version+"/solutionmanifest");
        URLConnection con = u.openConnection();
        
        File f = new java.io.File("RADS/solutions/"+ab+"/releases/" + version + "/solutionmanifest");
        new File(f.getParent()).mkdirs();
        f.createNewFile();
        
        try (InputStream in = con.getInputStream()) {
            try (OutputStream fo = new FileOutputStream(f)) {
                int read;
                byte[] buffer = new byte[2048];
                while((read = in.read(buffer)) != -1){
                    fo.write(buffer, 0, read);
                }
            }
        }
    }
    
    
    
    
    public void dumpConfig(String versions , String num) throws IOException{
        File f = new java.io.File("RADS/solutions/"+versions+"/releases/" + num + "/configurationmanifest");
        System.out.println("a");
        System.out.println("RADS/solutions/"+versions+"/releases/" + num + "/configurationmanifest");
        f.createNewFile();
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(f))) {
            bw.write("RADS Configuration Manifest\r\n" +
                    "1.0.0.0\r\n" +
                    language + "\r\n" +
                    "2\r\n" +
                    versions +"\r\n" +
                    versions+"_" + language + "\r\n");
        }
        
        File confdir = new java.io.File("RADS/solutions/"+versions+"/releases/" + num + "/deploy/DATA/cfg/defaults/");
        confdir.mkdirs();
        File conf = new File(confdir, "locale.cfg");
        
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(conf))) {
            String[] lang = language.split("_");
            lang[1] = lang[1].toUpperCase();
            bw.write("[General]\r\n" +
                     "LanguageLocaleRegion="+lang[0] + "_"+lang[1]);
        }
    }
    
    public void addPatchers(){
    	
        slnversion = LoLPatcher.getVersion("solutions", "lol_game_client_sln", server);
        String branch = (server.equals("PBE") ? "pbe" : "live");
        try {
            getSolutionManifest(slnversion, branch,"lol_game_client_sln");
            dumpConfig("lol_game_client_sln",slnversion);
        } catch (IOException ex) {
            Logger.getLogger(ConfigurationTask.class.getName()).log(Level.SEVERE, null, ex);
        }
    	
        
    	if(league_client){
	        new_slnversion = LoLPatcher.getVersion("solutions", "league_client_sln", server);
	        String new_branch = (server.equals("PBE") ? "pbe" : "live");
	        try {
	            getSolutionManifest(new_slnversion, new_branch,"league_client_sln");
	            dumpConfig("league_client_sln",new_slnversion);
	        } catch (IOException ex) {
	            Logger.getLogger(ConfigurationTask.class.getName()).log(Level.SEVERE, null, ex);
	        }
    	}  
        
        main.airversion = LoLPatcher.getVersion("projects", "lol_air_client", server);
        
        String clientConfigName = "lol_air_client_config"+(server.equals("PBE") ? "" : "_"+server.toLowerCase());
        
        String league_client_clientConfigName = "league_client";
        
        
        String gameversion = LoLPatcher.getVersion("projects", "lol_game_client", server);
        String leagueclientversion = LoLPatcher.getVersion("projects", "league_client", server);
        String airconfigversion = LoLPatcher.getVersion("projects", clientConfigName, server);
        String league_client_version = LoLPatcher.getVersion("projects", league_client_clientConfigName, server);
        String gamelanguageversion = LoLPatcher.getVersion("projects", "lol_game_client_"+language, server);
        
        String client_gamelanguageversion = LoLPatcher.getVersion("projects", "lol_game_client_"+language, server);
        
        
        if(main.purgeAfterwards && PURGE_UNWANTED_FILES){
            main.patchers.add(new ArchivePurgeTask("lol_game_client", gameversion, branch, "projects"));
            done = true;
            return;
        }
        
        if (lol_air_client)
        	main.patchers.add(new LoLPatcher(main.airversion, "lol_air_client", branch, main.ignoreS_OK, main.force));
        
        if (lol_game_client){
        	main.patchers.add(new LoLPatcher(gameversion, "lol_game_client", branch, main.ignoreS_OK, main.force));
        	main.patchers.add(new LoLPatcher(airconfigversion, clientConfigName, branch, main.ignoreS_OK, main.force));
        }
        
        if(lol_game_client_Lang)
        	main.patchers.add(new LoLPatcher(gamelanguageversion, "lol_game_client_"+language, branch, main.ignoreS_OK, main.force));
        
        
        
        if (league_client){
        	main.patchers.add(new LoLPatcher(leagueclientversion, "league_client", branch, main.ignoreS_OK, main.force));
        	main.patchers.add(new LoLPatcher(league_client_version, league_client_clientConfigName, branch, main.ignoreS_OK, main.force));
        }
        
        if(league_client_Lang)
        	main.patchers.add(new LoLPatcher(client_gamelanguageversion, "lol_game_client_"+language, branch, main.ignoreS_OK, main.force));
        
        if(league_client)
        	main.patchers.add(new SLNPatcher(leagueclientversion, new_slnversion, main.ignoreS_OK,"league_client"));
        
        if(lol_game_client)
        	main.patchers.add(new SLNPatcher(gameversion, slnversion, main.ignoreS_OK,"lol_game_client"));
        
        if (lol_air_client){
	        main.patchers.add(new RunTask(new Runnable() {
	            @Override
	            public void run() {
	                try {
	                	System.out.println("B");
	                	System.out.println("RADS/projects/lol_air_client/releases/" + main.airversion + "/deploy/locale.properties");
	                    File f = new java.io.File("RADS/projects/lol_air_client/releases/" + main.airversion + "/deploy/locale.properties");
	                    f.createNewFile();
	                    try (BufferedWriter bw = new BufferedWriter(new FileWriter(f))) {
	                        String[] lang = language.split("_");
	                        lang[1] = lang[1].toUpperCase();
	                        bw.write("locale=" + lang[0] + "_" + lang[1]);
	                    }
	                }   catch (IOException ex) {
	                    Logger.getLogger(ConfigurationTask.class.getName()).log(Level.SEVERE, null, ex);
	                }
	            }
	        }, "Locale config"));
        }
        
        done = true;
    }

    @Override
    public float getPercentage() {
        return percentage;
    }
}
