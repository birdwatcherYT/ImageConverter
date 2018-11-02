package imageconverter;

import static img.MyColor.*;
import java.util.*;
import java.awt.datatransfer.*;
import java.awt.dnd.*;
import java.awt.*;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;

public class ImageConverter extends JFrame {

    final static int WIDTH = 300, HEIGHT = 300;
    BufferedImage read, write;
    String filename;
    JComboBox cb, cbX, cbY;
    JCheckBox ch, alpha;
    JLabel la, la2, la3;
    JTextField t, __r, __g, __b;
    int width, height, scaleX, scaleY;

    public static void main(String[] args) {
        new ImageConverter();
    }

    ImageConverter() {
        super("画像処理");
        setSize(WIDTH, HEIGHT);
        setLayout(new FlowLayout());
        setBounds(100, 100, WIDTH, HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        new DropTarget(this, new Drop());

        String item[] = {"jpg", "gif", "png", "bmp"};
        String X[] = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
        String Y[] = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};

        cb = new JComboBox(item);
        ch = new JCheckBox("色の反転");
        alpha = new JCheckBox("透過色(RGB)=\n");
        la = new JLabel("出力ファイル形式");
        la2 = new JLabel("横方向の倍率");
        la3 = new JLabel("縦方向の倍率");
        cbX = new JComboBox(X);
        cbY = new JComboBox(Y);
        t = new JTextField();
        t.setPreferredSize(new Dimension(280, 30));
        __r = new JTextField("255");
        __r.setPreferredSize(new Dimension(50, 30));
        __g = new JTextField("255");
        __g.setPreferredSize(new Dimension(50, 30));
        __b = new JTextField("255");
        __b.setPreferredSize(new Dimension(50, 30));
        add(ch);
        add(la);
        add(cb);
        add(la2);
        add(cbX);
        add(la3);
        add(cbY);
        add(alpha);
        add(__r);
        add(__g);
        add(__b);
        add(t);
        setVisible(true);
        setResizable(false);
        setAlwaysOnTop(true);
        t.setEditable(false);
    }

    /**
     * 拡大 書き込み
     *
     * @param type フォーマット
     * @return 成功ならtrue
     */
    boolean scale(String type) {
        int r, g, b, _r = 0, _g = 0, _b = 0;

        boolean flag;
        if ((flag = alpha.isSelected() && (type.equals("gif") || type.equals("png")))) {
            try {
                _r = Integer.valueOf(__r.getText());
                _g = Integer.valueOf(__g.getText());
                _b = Integer.valueOf(__b.getText());
                if (_r < 0 || _g < 0 || _b < 0 || _r > 255 || _g > 255 || _b > 255) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "0から255までの整数値を入力してください");
                return false;
            }
        }

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int color = read.getRGB(x, y);
                if (ch.isSelected()) {
                    r = 255 - R(color);
                    g = 255 - G(color);
                    b = 255 - B(color);
                } else {
                    r = R(color);
                    g = G(color);
                    b = B(color);
                }

                for (int k = 0; k < scaleX; k++) {
                    for (int i = 0; i < scaleY; i++) {
                        if (flag && r == _r && g == _g && b == _b) {
                            write.setRGB((int) (scaleX * x + k), (int) (scaleY * y + i), RGB(r, g, b) & 0x00ffffff);
                        } else {
                            write.setRGB((int) (scaleX * x + k), (int) (scaleY * y + i), RGB(r, g, b));
                        }
                    }
                }
            }
        }
        return true;
    }

    /**
     * 読み書き
     *
     * @param str1 変換元のファイル名
     * @param str2 変換後のファイル名
     * @param type フォーマット
     * @return 成功ならtrue
     */
    boolean readwrite(String str1, String str2, String type) {
        try {

            File fp = new File(str1);
            read = ImageIO.read(fp);
            width = read.getWidth();
            height = read.getHeight();
            scaleX = Integer.valueOf((String) cbX.getSelectedItem());
            scaleY = Integer.valueOf((String) cbY.getSelectedItem());

            write = new BufferedImage((int) (scaleX * width), (int) (scaleY * height),
                    (alpha.isSelected() && (type.equals("gif") || type.equals("png"))) ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB
            );

            if (!scale(type)) {
                return false;
            }

            File fp2 = new File(str2);
            ImageIO.write(write, type, fp2);

        } catch (IOException ioe) {
            return false;
        }
        return true;
    }

    /**
     * D&Dのクラス
     */
    class Drop extends DropTargetAdapter {

        String str1, str2, type;

        Drop() {
        }

        public void drop(DropTargetDropEvent e) {
            type = (String) cb.getSelectedItem();
            try {
                Transferable transfer = e.getTransferable();
                if (transfer.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                    e.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
                    java.util.List fileList = (java.util.List) transfer.getTransferData(DataFlavor.javaFileListFlavor);
                    Iterator iterator = fileList.iterator();
                    while (iterator.hasNext()) {
                        File f = (File) iterator.next();
                        str1 = f.getPath();
                        filename = f.getName() + "を変換中";
                        t.setText(filename);
                        switch (str1.substring(str1.lastIndexOf('.') + 1)) {
                            case "jpeg":
                            case "jpg":
                            case "bmp":
                            case "png":
                            case "gif":
                            case "GIF":
                            case "BMP":
                            case "JPG":
                            case "JPEG":
                            case "PNG":
                                t.setText(filename);
                                str2 = str1.substring(0, str1.lastIndexOf('\\') + 1) + "convert_" + str1.substring(str1.lastIndexOf('\\') + 1, str1.lastIndexOf('.') + 1) + type;
                                if (!readwrite(str1, str2, type)) {
                                    JOptionPane.showMessageDialog(ImageConverter.this, "変換できませんでした");
                                    t.setText("");
                                    return;
                                }
                                break;
                            default:
                                JOptionPane.showMessageDialog(ImageConverter.this, "対応していないファイル形式です");
                                return;
                        }
                    }
                }
            } catch (UnsupportedFlavorException ufe) {
            } catch (IOException ioe) {
            }
            JOptionPane.showMessageDialog(ImageConverter.this, "完了");
            t.setText("");
        }
    }
}
