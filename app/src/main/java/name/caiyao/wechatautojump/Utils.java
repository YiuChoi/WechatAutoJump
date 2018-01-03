package name.caiyao.wechatautojump;

import android.content.Context;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 * Created by 蔡小木 on 2018/1/3.
 */

public class Utils {
    public static void copyFromAssetsToFile(String name, Context context){
        try {
            InputStream is = context.getAssets().open(name);
            FileOutputStream fos  = new FileOutputStream(context.getFilesDir()+"/"+name);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer))!=-1){
                fos.write(buffer,0,length);
                fos.flush();
            }
            is.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static  void suCmd(String[] ss,Process process){
        try {
            OutputStream outputStream = process.getOutputStream();
            for (String s: ss) {
                Log.i("TAG",s);
                outputStream.write((s+ " 2>&1\n").getBytes());
            }
            outputStream.close();
            BufferedReader inputStream = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = inputStream.readLine())!=null){
                Log.i("TAG",line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean testRoot() {
        ArrayList<String> output = new ArrayList<>();
        try {
            Process process = Runtime.getRuntime().exec("su");

            BufferedOutputStream shellInput = new BufferedOutputStream(
                    process.getOutputStream());
            BufferedReader shellOutput = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));
            String cmd = "ls -l /data/data/com.tencent.mm/MicroMsg";
            shellInput.write((cmd + " 2>&1\n").getBytes());
            shellInput.write("exit\n".getBytes());
            shellInput.flush();
            String line;
            while ((line = shellOutput.readLine()) != null) {
                output.add(line);
            }
            if (output.size() == 0) {
                return false;
            }
            process.waitFor();
            return true;
        } catch (IOException | InterruptedException e) {
            return false;
        }
    }
}
