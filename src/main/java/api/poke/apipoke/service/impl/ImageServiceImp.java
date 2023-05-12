package api.poke.apipoke.service.impl;

import api.poke.apipoke.callable.ThreadTask;
import api.poke.apipoke.service.ImageService;
import cn.hutool.core.img.gif.AnimatedGifEncoder;
import cn.hutool.core.img.gif.GifDecoder;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.Yaml;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

@Service
public class ImageServiceImp implements ImageService {
    private final int processNum = Runtime.getRuntime().availableProcessors();
    private final ExecutorService pool = new ThreadPoolExecutor(processNum, processNum, 1L, TimeUnit.SECONDS, new ArrayBlockingQueue<>(100));
    private Map<String, Object> config = null;

    @Override
    public ByteArrayOutputStream kd(String qq) throws Exception {
        if (config == null) initConfig();
        BufferedImage avatar = fetchAvatar(qq);
        return threadDealImage(avatar, "image/kd.gif", "kd");
    }

    @Override
    public ByteArrayOutputStream zyy(String qq) throws IOException {
        if (config == null) initConfig();
        BufferedImage avatar = fetchAvatar(qq);
        return customDealImage(avatar, "image/zyy.gif", "zyy");
    }

    @Override
    public ByteArrayOutputStream psj(String qq) throws IOException {
        BufferedImage avatar = fetchAvatar(qq);
        InputStream file = new ClassPathResource("image/psj.png").getInputStream();
        BufferedImage psj = ImageIO.read(file);
        BufferedImage img = new BufferedImage(psj.getWidth(), psj.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        g.translate(140, 215);
        g.rotate(-20 * Math.PI / 180);
        g.drawImage(avatar, -33, -35, 66, 66, null);
        g.rotate(20 * Math.PI / 180);
        g.drawImage(psj, -140, -215, null);
        g.dispose();
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ImageIO.write(img, "png", output);
        return output;
    }

    @Override
    public ByteArrayOutputStream mb(String qq) throws IOException {
        if (config == null) initConfig();
        BufferedImage avatar = fetchAvatar(qq);
        return customDealImage(avatar, "image/mb.gif", "mb");
    }

    @Override
    public ByteArrayOutputStream iKun(String qq) throws IOException {
        if (config == null) initConfig();
        BufferedImage avatar = fetchAvatar(qq);
        return customDealImage(avatar, "image/ik.gif", "ik");
    }

    private BufferedImage fetchAvatar(String qq) throws IOException {
        URL url = new URL(String.format("https://q1.qlogo.cn/g?b=qq&nk=%s&s=100", qq));
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        return ImageIO.read(con.getInputStream());
    }

    private void initConfig() throws IOException {
        Yaml yml = new Yaml();
        InputStream file = new ClassPathResource("config.yaml").getInputStream();
        config = yml.load(file);
    }

    private ByteArrayOutputStream customDealImage(BufferedImage avatar, String path, String cfg) throws IOException {
        InputStream file = (new ClassPathResource(path)).getInputStream();
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        GifDecoder d = new GifDecoder();
        d.read(file);
        AnimatedGifEncoder encoder = new AnimatedGifEncoder();
        encoder.start(output);
        encoder.setDelay(d.getDelay(0));
        encoder.setRepeat(0);
        int cnt = d.getFrameCount();
        ArrayList<ArrayList<Integer>> arr = (ArrayList<ArrayList<Integer>>) config.get(cfg);
        for (int i = 0; i < cnt; i++) {
            BufferedImage frame = d.getFrame(i);
            int width = frame.getWidth();
            int height = frame.getHeight();
            BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = img.createGraphics();
            int avaWidth = arr.get(i).get(1) - arr.get(i).get(3);
            int avaHeight = arr.get(i).get(2) - arr.get(i).get(0);
            double cenWidth = (arr.get(i).get(1) + arr.get(i).get(3)) / 2.0;
            double cenHeight = (arr.get(i).get(2) + arr.get(i).get(0)) / 2.0;
            int angle = 0;
            try {
                angle = arr.get(i).get(4);
            } catch (Exception ignored) {
            }
            if (avaHeight != 0 && avaWidth != 0) {
                g.translate(cenWidth, cenHeight);
                if (angle != 0) g.rotate(angle * Math.PI / 180.0);
                g.drawImage(avatar, -avaWidth / 2, -avaHeight / 2, avaWidth, avaHeight, null);
                if (angle != 0) g.rotate(-angle * Math.PI / 180.0);
                g.translate(-cenWidth, -cenHeight);
            }
            g.drawImage(frame, 0, 0, width, height, null);
            g.dispose();
            encoder.addFrame(img);
        }
        encoder.finish();
        return output;
    }

    private ByteArrayOutputStream threadDealImage(BufferedImage avatar, String path, String cfg) throws IOException, ExecutionException, InterruptedException, TimeoutException {
        InputStream file = (new ClassPathResource(path)).getInputStream();
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        GifDecoder d = new GifDecoder();
        d.read(file);
        AnimatedGifEncoder encoder = new AnimatedGifEncoder();
        encoder.start(output);
        encoder.setRepeat(0);
        encoder.setDelay(d.getDelay(0));
        ArrayList<ArrayList<Integer>> arr = (ArrayList<ArrayList<Integer>>) config.get(cfg);
        int frameCnt = d.getFrameCount();
        int threadCnt = frameCnt / 10;
        List<Future<List<BufferedImage>>> res = new ArrayList<>();
        for (int i = 0; i < threadCnt; i++) {
            List<BufferedImage> tasks = new ArrayList<>();
            for (int j = 0; j < 10 && i * 10 + j < frameCnt; j++) {
                tasks.add(d.getFrame(i * 10 + j));
            }
            ThreadTask task = new ThreadTask(tasks, arr, avatar, i * 10);
            res.add(pool.submit(task));
        }
        for (Future<List<BufferedImage>> re : res) {
            List<BufferedImage> subFrame = re.get(2500, TimeUnit.MILLISECONDS);
            subFrame.forEach(encoder::addFrame);
        }
        encoder.finish();
        return output;
    }
}
