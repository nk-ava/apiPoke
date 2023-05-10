package api.poke.apipoke.service.impl;

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
import java.util.Map;

@Service
public class ImageServiceImp implements ImageService {
    private Map<String, Object> config = null;

    @Override
    public ByteArrayOutputStream kd(String qq) throws IOException {
        if (config == null) initConfig();
        BufferedImage avatar = fetchAvatar(qq);
        return customDealImage(avatar, "image/kd.gif", "kd");
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
        File file = new ClassPathResource("image/psj.png").getFile();
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

    private BufferedImage fetchAvatar(String qq) throws IOException {
        URL url = new URL(String.format("https://q1.qlogo.cn/g?b=qq&nk=%s&s=640", qq));
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        return ImageIO.read(con.getInputStream());
    }

    private void initConfig() throws IOException {
        Yaml yml = new Yaml();
        File file = new ClassPathResource("config.yaml").getFile();
        InputStream input = new FileInputStream(file);
        config = yml.load(input);
    }

    private ByteArrayOutputStream customDealImage(BufferedImage avatar, String path, String cfg) throws IOException {
        File file = (new ClassPathResource(path)).getFile();
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        GifDecoder d = new GifDecoder();
        d.read(file.getPath());
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
            g.translate(cenWidth, cenHeight);
            g.drawImage(avatar, -avaWidth / 2, -avaHeight / 2, avaWidth, avaHeight, null);
            g.translate(-cenWidth, -cenHeight);
            g.drawImage(frame, 0, 0, width, height, null);
            g.dispose();
            encoder.addFrame(img);
        }
        encoder.finish();
        return output;
    }
}
