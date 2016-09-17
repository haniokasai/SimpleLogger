package com.haniokasai.nukkit.SimpleLogger;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;

import cn.nukkit.Server;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;



public class Main extends PluginBase implements Listener{

Config data;
String time;
int type=2;
int trigger =1;
String logformat="hh-mm";


	public void onEnable() {

		 this.getServer().getPluginManager().registerEvents(this, this);
		getDataFolder().mkdir();

		@SuppressWarnings("deprecation")
		Config config = new Config(
                new File(getDataFolder(), "config.yml"),Config.YAML,
                new LinkedHashMap<String, Object>() {
                    {
                    	put("deleteday", 10);
                    	put("logtype(1=unixtime,2=normaltime)", 2);
                    	put("logtrigger(1=joinquit,2=interval,3=1and2",2);
                    	put("logintervalsecond",900);
                    	put("logformat","hh-mm");
                    }
                });
        config.save();
        type=config.getInt("logtype(1=unixtime,2=normaltime)");
        trigger=config.getInt("logtrigger(1=joinquit,2=interval,3=1and2",2);
        logformat=config.getString("logformat");



        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date ntime = new Date(System.currentTimeMillis());
        time = sdf.format(ntime);

        File file = new File(this.getDataFolder().toString());
        File files[] = file.listFiles();

	    Date date1 = null;
	    Date date2 = null;
        for (int i=0; i<files.length; i++) {
        	if(files[i].getName().endsWith(".json")){
			try {
				date1 = sdf.parse(files[i].getName());
				date2  = sdf.parse(time);
	    	    long datetime1 = date1.getTime();
	    	    long datetime2 = date2.getTime();
	    	    long one_date_time = 1000 * 60 * 60 * 24;
	    	    long diffDays = (datetime1 - datetime2) / one_date_time;
	    	    if(diffDays  >config.getInt("deleteday")){
	    	    	files[i].delete();
	    	    }
			} catch (ParseException e) {
				e.printStackTrace();
			}
        }

        Server.getInstance().getScheduler().scheduleDelayedTask(new Runnable() {
        	@Override
        	public void run() {
        		save();
        	}
        },20*2);

        if(trigger>1){
    	Server.getInstance().getScheduler().scheduleRepeatingTask(new Runnable() {
    		@Override
			public void run() {
				save();
			}
    	},20*60*config.getInt("logintervalsecond"));
        }

        }





        cre8data();

        this.getServer().getLogger().info("[SimpleLogger] Loaded");

}

	@EventHandler
	public void join(PlayerJoinEvent event){
		if(trigger!=2){
			save();
		}
	}

	@EventHandler
	public void quit(PlayerQuitEvent event){
		if(trigger!=2){
			save();
		}
	}

	public void save(){
		if(type==1){
			data.set(Integer.toString((int) (System.currentTimeMillis()/1000) ), Server.getInstance().getOnlinePlayers().size());
		}else{
			SimpleDateFormat sdf = new SimpleDateFormat(logformat);
	        Date nowTime = new Date(System.currentTimeMillis());
	        String nowatime =sdf.format(nowTime);
			data.set(nowatime.toString(), Server.getInstance().getOnlinePlayers().size());
		}
		data.save();
	}

	public void cre8data(){


		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date nowTime = new Date(System.currentTimeMillis());
        String nowatime;
        nowatime =sdf.format(nowTime);

        if(nowatime!=time){
        	data = new Config(this.getDataFolder()+"/"+nowatime+".json",Config.JSON);
        }

	}


}