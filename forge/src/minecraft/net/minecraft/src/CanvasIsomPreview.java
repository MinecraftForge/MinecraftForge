package net.minecraft.src;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferStrategy;
import java.awt.image.ImageObserver;
import java.io.File;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class CanvasIsomPreview extends Canvas implements KeyListener, MouseListener, MouseMotionListener, Runnable
{
    private int currentRender = 0;
    private int zoom = 2;
    private boolean showHelp = true;
    private World level;
    private File workDir = this.getWorkingDirectory();
    private boolean running = true;
    private List zonesToRender = Collections.synchronizedList(new LinkedList());
    private IsoImageBuffer[][] zoneMap = new IsoImageBuffer[64][64];
    private int field_1785_i;
    private int field_1784_j;
    private int field_1783_k;
    private int field_1782_l;

    public File getWorkingDirectory()
    {
        if (this.workDir == null)
        {
            this.workDir = this.getWorkingDirectory("minecraft");
        }

        return this.workDir;
    }

    public File getWorkingDirectory(String par1Str)
    {
        String var2 = System.getProperty("user.home", ".");
        File var3;

        switch (OsMap.field_1193_a[getPlatform().ordinal()])
        {
            case 1:
            case 2:
                var3 = new File(var2, '.' + par1Str + '/');
                break;

            case 3:
                String var4 = System.getenv("APPDATA");

                if (var4 != null)
                {
                    var3 = new File(var4, "." + par1Str + '/');
                }
                else
                {
                    var3 = new File(var2, '.' + par1Str + '/');
                }

                break;

            case 4:
                var3 = new File(var2, "Library/Application Support/" + par1Str);
                break;

            default:
                var3 = new File(var2, par1Str + '/');
        }

        if (!var3.exists() && !var3.mkdirs())
        {
            throw new RuntimeException("The working directory could not be created: " + var3);
        }
        else
        {
            return var3;
        }
    }

    private static EnumOS1 getPlatform()
    {
        String var0 = System.getProperty("os.name").toLowerCase();
        return var0.contains("win") ? EnumOS1.windows : (var0.contains("mac") ? EnumOS1.macos : (var0.contains("solaris") ? EnumOS1.solaris : (var0.contains("sunos") ? EnumOS1.solaris : (var0.contains("linux") ? EnumOS1.linux : (var0.contains("unix") ? EnumOS1.linux : EnumOS1.unknown)))));
    }

    public CanvasIsomPreview()
    {
        for (int var1 = 0; var1 < 64; ++var1)
        {
            for (int var2 = 0; var2 < 64; ++var2)
            {
                this.zoneMap[var1][var2] = new IsoImageBuffer((World)null, var1, var2);
            }
        }

        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        this.addKeyListener(this);
        this.setFocusable(true);
        this.requestFocus();
        this.setBackground(Color.red);
    }

    public void loadLevel(String par1Str)
    {
        this.field_1785_i = this.field_1784_j = 0;
        this.level = new World(new SaveHandler(new File(this.workDir, "saves"), par1Str, false), par1Str, new WorldSettings((new Random()).nextLong(), 0, true, false, WorldType.field_48635_b));
        this.level.skylightSubtracted = 0;
        List var2 = this.zonesToRender;

        synchronized (this.zonesToRender)
        {
            this.zonesToRender.clear();

            for (int var3 = 0; var3 < 64; ++var3)
            {
                for (int var4 = 0; var4 < 64; ++var4)
                {
                    this.zoneMap[var3][var4].init(this.level, var3, var4);
                }
            }
        }
    }

    private void setBrightness(int par1)
    {
        List var2 = this.zonesToRender;

        synchronized (this.zonesToRender)
        {
            this.level.skylightSubtracted = par1;
            this.zonesToRender.clear();

            for (int var3 = 0; var3 < 64; ++var3)
            {
                for (int var4 = 0; var4 < 64; ++var4)
                {
                    this.zoneMap[var3][var4].init(this.level, var3, var4);
                }
            }
        }
    }

    public void start()
    {
        (new ThreadRunIsoClient(this)).start();

        for (int var1 = 0; var1 < 8; ++var1)
        {
            (new Thread(this)).start();
        }
    }

    public void stop()
    {
        this.running = false;
    }

    private IsoImageBuffer getZone(int par1, int par2)
    {
        int var3 = par1 & 63;
        int var4 = par2 & 63;
        IsoImageBuffer var5 = this.zoneMap[var3][var4];

        if (var5.x == par1 && var5.y == par2)
        {
            return var5;
        }
        else
        {
            List var6 = this.zonesToRender;

            synchronized (this.zonesToRender)
            {
                this.zonesToRender.remove(var5);
            }

            var5.init(par1, par2);
            return var5;
        }
    }

    public void run()
    {
        TerrainTextureManager var1 = new TerrainTextureManager();

        while (this.running)
        {
            IsoImageBuffer var2 = null;
            List var3 = this.zonesToRender;

            synchronized (this.zonesToRender)
            {
                if (this.zonesToRender.size() > 0)
                {
                    var2 = (IsoImageBuffer)this.zonesToRender.remove(0);
                }
            }

            if (var2 != null)
            {
                if (this.currentRender - var2.lastVisible < 2)
                {
                    var1.render(var2);
                    this.repaint();
                }
                else
                {
                    var2.addedToRenderQueue = false;
                }
            }

            try
            {
                Thread.sleep(2L);
            }
            catch (InterruptedException var5)
            {
                var5.printStackTrace();
            }
        }
    }

    public void update(Graphics par1Graphics) {}

    public void paint(Graphics par1Graphics) {}

    public void render()
    {
        BufferStrategy var1 = this.getBufferStrategy();

        if (var1 == null)
        {
            this.createBufferStrategy(2);
        }
        else
        {
            this.render((Graphics2D)var1.getDrawGraphics());
            var1.show();
        }
    }

    public void render(Graphics2D par1Graphics2D)
    {
        ++this.currentRender;
        AffineTransform var2 = par1Graphics2D.getTransform();
        par1Graphics2D.setClip(0, 0, this.getWidth(), this.getHeight());
        par1Graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        par1Graphics2D.translate(this.getWidth() / 2, this.getHeight() / 2);
        par1Graphics2D.scale((double)this.zoom, (double)this.zoom);
        par1Graphics2D.translate(this.field_1785_i, this.field_1784_j);

        if (this.level != null)
        {
            ChunkCoordinates var3 = this.level.getSpawnPoint();
            par1Graphics2D.translate(-(var3.posX + var3.posZ), -(-var3.posX + var3.posZ) + 64);
        }

        Rectangle var17 = par1Graphics2D.getClipBounds();
        par1Graphics2D.setColor(new Color(-15724512));
        par1Graphics2D.fillRect(var17.x, var17.y, var17.width, var17.height);
        byte var4 = 16;
        byte var5 = 3;
        int var6 = var17.x / var4 / 2 - 2 - var5;
        int var7 = (var17.x + var17.width) / var4 / 2 + 1 + var5;
        int var8 = var17.y / var4 - 1 - var5 * 2;
        int var9 = (var17.y + var17.height + 16 + 128) / var4 + 1 + var5 * 2;
        int var10;

        for (var10 = var8; var10 <= var9; ++var10)
        {
            for (int var11 = var6; var11 <= var7; ++var11)
            {
                int var12 = var11 - (var10 >> 1);
                int var13 = var11 + (var10 + 1 >> 1);
                IsoImageBuffer var14 = this.getZone(var12, var13);
                var14.lastVisible = this.currentRender;

                if (!var14.rendered)
                {
                    if (!var14.addedToRenderQueue)
                    {
                        var14.addedToRenderQueue = true;
                        this.zonesToRender.add(var14);
                    }
                }
                else
                {
                    var14.addedToRenderQueue = false;

                    if (!var14.noContent)
                    {
                        int var15 = var11 * var4 * 2 + (var10 & 1) * var4;
                        int var16 = var10 * var4 - 128 - 16;
                        par1Graphics2D.drawImage(var14.image, var15, var16, (ImageObserver)null);
                    }
                }
            }
        }

        if (this.showHelp)
        {
            par1Graphics2D.setTransform(var2);
            var10 = this.getHeight() - 32 - 4;
            par1Graphics2D.setColor(new Color(Integer.MIN_VALUE, true));
            par1Graphics2D.fillRect(4, this.getHeight() - 32 - 4, this.getWidth() - 8, 32);
            par1Graphics2D.setColor(Color.WHITE);
            String var18 = "F1 - F5: load levels   |   0-9: Set time of day   |   Space: return to spawn   |   Double click: zoom   |   Escape: hide this text";
            par1Graphics2D.drawString(var18, this.getWidth() / 2 - par1Graphics2D.getFontMetrics().stringWidth(var18) / 2, var10 + 20);
        }

        par1Graphics2D.dispose();
    }

    public void mouseDragged(MouseEvent par1MouseEvent)
    {
        int var2 = par1MouseEvent.getX() / this.zoom;
        int var3 = par1MouseEvent.getY() / this.zoom;
        this.field_1785_i += var2 - this.field_1783_k;
        this.field_1784_j += var3 - this.field_1782_l;
        this.field_1783_k = var2;
        this.field_1782_l = var3;
        this.repaint();
    }

    public void mouseMoved(MouseEvent par1MouseEvent) {}

    public void mouseClicked(MouseEvent par1MouseEvent)
    {
        if (par1MouseEvent.getClickCount() == 2)
        {
            this.zoom = 3 - this.zoom;
            this.repaint();
        }
    }

    public void mouseEntered(MouseEvent par1MouseEvent) {}

    public void mouseExited(MouseEvent par1MouseEvent) {}

    public void mousePressed(MouseEvent par1MouseEvent)
    {
        int var2 = par1MouseEvent.getX() / this.zoom;
        int var3 = par1MouseEvent.getY() / this.zoom;
        this.field_1783_k = var2;
        this.field_1782_l = var3;
    }

    public void mouseReleased(MouseEvent par1MouseEvent) {}

    public void keyPressed(KeyEvent par1KeyEvent)
    {
        if (par1KeyEvent.getKeyCode() == 48)
        {
            this.setBrightness(11);
        }

        if (par1KeyEvent.getKeyCode() == 49)
        {
            this.setBrightness(10);
        }

        if (par1KeyEvent.getKeyCode() == 50)
        {
            this.setBrightness(9);
        }

        if (par1KeyEvent.getKeyCode() == 51)
        {
            this.setBrightness(7);
        }

        if (par1KeyEvent.getKeyCode() == 52)
        {
            this.setBrightness(6);
        }

        if (par1KeyEvent.getKeyCode() == 53)
        {
            this.setBrightness(5);
        }

        if (par1KeyEvent.getKeyCode() == 54)
        {
            this.setBrightness(3);
        }

        if (par1KeyEvent.getKeyCode() == 55)
        {
            this.setBrightness(2);
        }

        if (par1KeyEvent.getKeyCode() == 56)
        {
            this.setBrightness(1);
        }

        if (par1KeyEvent.getKeyCode() == 57)
        {
            this.setBrightness(0);
        }

        if (par1KeyEvent.getKeyCode() == 112)
        {
            this.loadLevel("World1");
        }

        if (par1KeyEvent.getKeyCode() == 113)
        {
            this.loadLevel("World2");
        }

        if (par1KeyEvent.getKeyCode() == 114)
        {
            this.loadLevel("World3");
        }

        if (par1KeyEvent.getKeyCode() == 115)
        {
            this.loadLevel("World4");
        }

        if (par1KeyEvent.getKeyCode() == 116)
        {
            this.loadLevel("World5");
        }

        if (par1KeyEvent.getKeyCode() == 32)
        {
            this.field_1785_i = this.field_1784_j = 0;
        }

        if (par1KeyEvent.getKeyCode() == 27)
        {
            this.showHelp = !this.showHelp;
        }

        this.repaint();
    }

    public void keyReleased(KeyEvent par1KeyEvent) {}

    public void keyTyped(KeyEvent par1KeyEvent) {}

    static boolean isRunning(CanvasIsomPreview par0CanvasIsomPreview)
    {
        return par0CanvasIsomPreview.running;
    }
}
