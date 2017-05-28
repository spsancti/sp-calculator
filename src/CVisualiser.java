import java.text.DecimalFormat;


public class CVisualiser {

    private DecimalFormat mCeil 	= new DecimalFormat(  "################"); 	//[16]
    private DecimalFormat mFraction = new DecimalFormat("#.###############");	//[1].[16]
    private DecimalFormat mExp 		= new DecimalFormat("#.###############E00");//[1].[15]E[2]

    public String doFormat(double d) {
        double abs = Math.abs(d);
        String res = "";
        System.out.println(d);
        //check 0, 0...10, 10...infinity
        
        if(abs >= 10.0 && abs <= 9999999999999999.) 
        	res = findAppropriateFormat(abs);          
        else if(abs > 0.00001 && abs < 10) 
        	res = mFraction.format(abs);
        else if(abs == 0.0) 
        	res = "0";
        else 
        {
        	res = mExp.format(abs);
        	String tmp[] = res.split("E");
        	
            if(!tmp[0].contains(",")) //ceil 
                tmp[0] += ",";                      
            if(!tmp[1].startsWith("-"))  //fraction
                tmp[1] = "+" + tmp[1];
            
        	res = tmp[0] + "e" + tmp[1];
        }
        if(d < 0)            
        	res = "-" + res;
                
        if(res.endsWith(","))             
        	res = res.substring(0, res.length() - 1);        
        else if(res.endsWith(",0"))             
        	res = res.substring(0, res.length() - 2);        
        
        return res;
    }

    private String findAppropriateFormat(double d) {
        int ceilLength = mCeil.format(d).length();
        
        String format = "";
        
        for(int i = 0; i < ceilLength; i++) 
        	format += "#";
        
        if(ceilLength < 16) {
        	format += ".";
            
        	for(int i = 0; i < 16 - ceilLength; i++) 
            	format += "#";
            
        }
       
        DecimalFormat totalFormat = new DecimalFormat(format);
        
        return totalFormat.format(d);
    }
}
