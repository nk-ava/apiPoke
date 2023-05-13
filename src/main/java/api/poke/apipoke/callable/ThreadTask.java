package api.poke.apipoke.callable;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class ThreadTask implements Callable<List<BufferedImage>> {
    private List<BufferedImage> tasks = null;
    private ArrayList<ArrayList<Integer>> arr = null;
    private BufferedImage avatar = null;
    private int offset = 0;

    public ThreadTask(List<BufferedImage> tasks, ArrayList<ArrayList<Integer>> arr, BufferedImage avatar, int offset) {
        this.tasks = tasks;
        this.arr = arr;
        this.avatar = avatar;
        this.offset = offset;
    }

    @Override
    public List<BufferedImage> call() throws Exception {
        List<BufferedImage> ans = new ArrayList<BufferedImage>();
        for (int i = 0; i < tasks.size(); i++) {
            BufferedImage frame = tasks.get(i);
            int width = frame.getWidth();
            int height = frame.getHeight();
            BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = img.createGraphics();
            if (arr != null) {
                int avaWidth = arr.get(offset + i).get(1) - arr.get(offset + i).get(3);
                int avaHeight = arr.get(offset + i).get(2) - arr.get(offset + i).get(0);
                double cenWidth = (arr.get(offset + i).get(1) + arr.get(offset + i).get(3)) / 2.0;
                double cenHeight = (arr.get(offset + i).get(2) + arr.get(offset + i).get(0)) / 2.0;
                g.translate(cenWidth, cenHeight);
                g.drawImage(avatar, -avaWidth / 2, -avaHeight / 2, avaWidth, avaHeight, null);
                g.translate(-cenWidth, -cenHeight);
            } else {
                g.drawImage(avatar, 0, 0, width, height, null);
            }
            g.drawImage(frame, 0, 0, width, height, null);
            g.dispose();
            ans.add(img);
        }
        return ans;
    }
}
