/*
 * Minecraft Forge
 * Copyright (c) 2016-2019.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.fml.loading.progress;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.stb.STBEasyFont;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Base64;
import java.util.List;
import java.util.function.IntSupplier;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

class ClientVisualization implements EarlyProgressVisualization.Visualization {
    private final int screenWidth = 854;
    private final int screenHeight = 480;
    private long window;

    private static final String ICON =
            "iVBORw0KGgoAAAANSUhEUgAAAHAAAABwCAYAAADG4PRLAAA3xXpUWHRSYXcgcHJvZmlsZSB0eXBl" +
            "IGV4aWYAAHjarZxpsuy6sV7/cxRvCATREcNBxwjPwMP3Wqijqys92RF2+B7pNLWrWCSQ+TWJBK79" +
            "P//Hd/3Xf/1XeFJ6r5TrW1opN/+lltrT+ct7//77/RnudH4//9U/P+Lf//L69dcPHl6K/Bl//yz7" +
            "z/s7r+e/XSj9eX386+tXnX+u8/650J8f/OOC0W9++Muf971/LhSf3+vhz7+v9udzPf3tcf78/5vP" +
            "+XEevx/9+79TZTBW5nrxuZ4dQ7z5/fVbIncQW+znzx5jDM/v1c6/fSXF+p/H7vrrr/82eH/97d/G" +
            "7u5/Xo//OhTXXf68ofzbGP15PeT/PHZnhP5+R+Gf3/wvP3jb891//+/vY/et9/v27+l6KoxUuf48" +
            "1D8e5fyNNzKcKZ6PFX5V/p/5ez2/Gr9eHnEyY4vZHPyaV2jhYbS/kMIKPXxhnz9nmNxievZT+fN5" +
            "5hPPa2+sT3vmmYzkr/A9lelZV3yZlcmsRV5+/rqXcL63ne+b4eWbV+CdT+BigU/8t1/Xf3rx/+XX" +
            "Xxf6PkM3hPv9a6y4r8eY5jacOX/nXUxI+P6MaT7je35df4ub+28TG5nBfIb55QH7PX6XGDn8M7bi" +
            "mefI+/KdrvuXGqGuPxdgiPjuzM0QyyncJcQcSrjr89QQGMeX+enc+RPTM5iBkPOzwvUxNzEWJud9" +
            "/G4+U8N575Of38tACxORY4mVqSGBmKyUMvFT00sM9RxzunLOJdf85pZ7iSWVXEqpRYzqNdZUcy21" +
            "1re22t/4pje/5a3v+7a3t6dFICy30urV3tZa73xp59KdT3fe0ft4Rhxp5FFGHe9oo0/CZ6aZZ5l1" +
            "vrPNvp4VF+m/yqrXeldbfYdNKO208y677ne33T9i7Ytf+vJXvvq9X/v6X7P2Z1b/ddbCv83c/3nW" +
            "wp9Zc8bSeV/956zxcq3/uEQQTrJzxow9KTDj1RkgoB/n7H5DSo8z55zd7SEp8sOshezkrOCMMYNp" +
            "hyd/4a+5++fM/R/n7crp/2renv/dzF1O3f+Pmbucuj8z99/n7T/M2uqHUeKZILPQMb3jB7Dxpv68" +
            "/A887osHEDnSjPEt837iF3tba6bCs79b7J177bVCe3jmZ3HRNxBHvZT3jpF5HY1Hv/3ZO29uInFH" +
            "BFP43reWxN/77PX9EqGwdswrf2/7cuYHC6K+njTrvus3ehmzxny3nRiNVcvcOz+J++XV+JAI93rG" +
            "eJ6xVmI6mKvBlO30CpzflTpfnB7AqLWQ9vccGourcOGXiIHLSnwYXkJs8ekn5zgH055iy4z3O7/w" +
            "9p2umvNIZffQW/sK48NwrFb3jj3sOFaZKZTeylcWN/OUwNvqHVrcK+c+W84LOn3T9THzO93nM0xU" +
            "aKm+O3KDu8S7ro+X40NeMrG1Pj2vxuiD2+8S858Sg+/J70WElLdzVYKKnz393WTX/Bi69M0NY3R5" +
            "O1WemfgqdTBkZX9kDtO0M1GRcyJpx4q1xlSblLiISwRTg//iIIsmd9dy5XvCu8tIZEu/W5yb4Npt" +
            "oh94oLIy9HXdOd+9tkB0EtnTAAqpEjaEGRkLlzHifa2X68Bi/Gx1piNxM0TQXE/pvPiMq6zYvznm" +
            "qImwY7iY6vtdc4y0Ysk5ZIam3aRIn8x7/5jdBM+1O/GYX1/p+Zi19+ob3Vdesqzeq/cyJ6m3aync" +
            "zZfnKnltiJYf5dHDM0hYUo9Uii00XtgwfwdzrjDvXL7xqunWnoR4zu+zdm2kPIM1YPT5MjHfrszr" +
            "M77KBUCgnVJ54qpztrtMCPLEVCf0e0eKws5f47bCCzrcfcxG9MS9H2OZmXgZm8xtxc0D8YQzdgO9" +
            "z2ueCN01znePtYn6CV+FEdOcL/PMN7e20iBAX6YqBtIm1hWegibefB9ZdINpF2DzFh6/PFzoi2XN" +
            "72aCCpGU35cYCETbXk7gJpU7U78fYokvJE15xt03iDSv2lJoY6J27n3zpRvkYXLexYCGVqRgbnON" +
            "dL+vdzrHQ2ZtUqEQlAQZ07TmRLFN04IvI4kYcAIjM2wN9Im9Pqk0ng02f/MTIUK+mvBZY7ylEpBl" +
            "gKfz5WniVQCXBaAz2QWQfOL75FEbAmswb4uAZPZi2fsjzRZIzyMuxdtz96/XkRORTI5dxP7De8A1" +
            "8B3WacrH1Cd5RGQ8ZNfTwGfu4f0M4sEfT23c3mZEgXByNdaA8o9E5zPLNjdE1BoGzoA5jB8R2Wuo" +
            "Fb4KfcRnbAji2e0G22/p+bsnovIN+Z3XekAF8gaAhrzuwE+IUFCZ2R3hmz3tPeWACWndozISQCU0" +
            "lxqjUMH6zSPkAosEUACqWgw4Sqy+JHNZaMZ3DCbypApB2JACDVCATh2kGB3rtw/4LeUx6vVksP+N" +
            "JgB3T/whdkCM9iXIlwyGTD94CkQZuQ4yh0vvzuNCLUEPBaGP2vNl5gDQbyt76DvAxbuCYMvHA1US" +
            "j5a+SIAZCHXHG4gllYn2xCxWqK7Opc2KrX9l3BDaJm7qE4C/Ba1Ub/HexO/3TFTA2IQ8EwB+w14d" +
            "znqB0JnhkQUYX6hziR+oXnx14SnzHBlxAidNpMC4kR4J8Bc9UevYsY/p4kMoRuI1bCgfaLzmgZq6" +
            "amBayjth2A178iNIdTNaKKIXZOh91yHNwiTcQxg9k7Yic1ftpItURIsQr410PNGyOgQPShAUiiZy" +
            "/CP4NqzKTwM39vZEvN2APmP/PI0UaeEKAGwEIp4JHCxYE/5Fk2zYZ45+r9QYB/gu5fAWAvNTKWFM" +
            "iO2CsJmdFyJPd9VdySaCuOdseMlN92h+Kvt+6BxWKIQAyiC+hNjLWN8kAXkyeUQCufZ2XzFxg3ml" +
            "b8R7BCYcnIUPbpKUACjPPbjBoPiCm8ehoQwnzgJyF7Qbw8gIw7ToD3CpIwgJDUJ9gSLEr3KdeAJy" +
            "iKN1/2TBfnueaZSlSAtcUD0TzAdlDWy67+cEPZCEAnpnzWgZnjmPENdYA4QgPtMA8icphpRDNzEH" +
            "+7DFSvEr8QIdYDwFR38SiPeidSuZMpBqlY+QnyT3u3cHWABKEBgxDNKuTf4hhBB8vb9IP2+WqMqP" +
            "YU20HKcoF/TFjT+LtEcpr/FBJzf0D1psBA44n5FRX1HaPGlcJDj3uVBE5Fp+EB1yFmO1Xri1LIhT" +
            "WQgZFz0rWMqgEqY7rgfe3wF3dANi18STmqEpo1AAmUGedm8JSUl2ImvrTc5WayJkLyKFkK4Zsitl" +
            "3Z1QhHFghwuZ/IVbI1siVM4DVYJzxxfkHv1lxhsggpTh9iupjzZCioOARI3wkwXKPD8we3TyG+ZP" +
            "c7S9Puj921AGFgB26Q02YdoQ6KBCDQXHWsnePE2qgVIBtQMSzTgCx1G5iFwegciZTAbSkTFkxuGn" +
            "BuIw1lPo3wQokfpxD/XbHxH/BWQIT3LtF/gHND/w8kTMl1BoZLnwFF7ei64oyH1Y6h7zbkwWnpgw" +
            "gv6cwtZ3dozQpKgAnqEvzG7ko01a+3z4jr3afAU+JbcG0xSgBen/fpUMasocIDJUdGm6MEComrxX" +
            "R7mlGslCMLNk03OgV2ANQAHEJMYauc90MNQJ+iPeDsFJLl9jsIniWcC93F8eLnHX62OOCBKgCGWN" +
            "ppvE6Y9VC8M9nlZ8DuAFoSqRw2YX1NlU7xnoRgoT94wiPwSKuUrUWYGqXDhr1mvA8AAFsyHgwFll" +
            "VgChw+DRZveLkfwh+xx3lI8ZRuQRBi+gUPAW6Abi+c58EH5E9WLUBlH0NIkci3DtLrtiUipxgY5V" +
            "SsBaSG0S9GEUXsA2ENGrDoakH4pfkDzx/XxzARdoia9eiUgGGhtgsybMhwzoZHyNRIcmaLex0aIJ" +
            "KAJxMGYR5yYjS2AMjjyVxmjX3RYCDgfxzhDxdZW3d/xRDhhsniHtd3CXgCBeh68nvADlgWb9FpDi" +
            "96aO37mQG1Mi/VCQOb1NMVThcVk6D4wV2INMQ17hPvYOQi5SYIoRCKvdXqeIgMT2wEqoXKAUN60+" +
            "v3cHLUlf3OwDi/1qb1jIGZgukOxGSvKj+QztVDuUdQl6EmHfDDngqNebpyqJAiMpRvRqDXRDmT1W" +
            "ZqLjPzSPZDP3gfQk0C+YDgceUGRg4huwCEz5sIbATZFyBc8LXvFZ3gmQWK3b8DxqC67Ct3TEHt94" +
            "4a1JoaUNDTd8bG0O7Qx5ImE7sI7iIs8sPwSxQ0Rl0E0ZUO/WeEC1ZeNpsW8Nnzt5kpYGXyXnwE8Q" +
            "EZhtyWLw5EifiYx5SMqKfK7EGtdYxOIjXwIji+FljmFE8KcFwuFFxFaUFq56IS3m23jsXDGiD/QG" +
            "lZL9RAQZSbyhI+JwsGP62uL64yPUSs+R9wAN2Cv4g+kF3IlLgAq4czhQbGB3lBeJOQKKB0euTOKI" +
            "nMs38gJViHhDQ2H7+Q0eYTKI4dYlgzoD90H4RaM8JWZiyx8LEbU2lL3AHyzKwLkgmEB6fC0DRaS/" +
            "h5KndZSJgRgZQkH5QqQAf9wvEXMTZeF5ED/xspTdED4F5FDqAj33W9DPASkBaE0EMvzWsf96X+Cx" +
            "3RnZVRCiZFrUMBCm9QI8xiQl8GCFz4I7xFRBw0L+qGsAGbwGdzDlE54+Guq1WI5fG6AdijRp5C7k" +
            "EVIBakKHgOMwKzJIVcq4DoLulb4/YIChnmtyc5uMDxJu0yQXKA9TMi5kNISDFLoBZEa9lib5Mlwf" +
            "M4eVxJCjMwoojKxM4iG3wD2T+Ki59ZB4uGBEBDhOaKVKppPvDOSzBtmE/kYF7ndmxIAVEcz20RWk" +
            "fajCbNx42qJE1cZe/BMaRcYqSJcqqgCG4UEbFisSpAXgqxEhXXsgPpjRthsB7uSAloUQnfOCNfbH" +
            "A5SO7kUKc/EbNBlkZIPMNpzcvAg3jseK7edX4I+8nxBIjwdvHWe4lAHgiAoGdYMVHB1kHUTk+8IR" +
            "VrdQdNZRGrCpx085ASURv0EAz3dILSVdnftk7JPfREJnMZI5g23Lw731qZFYqGGctcWoyR1/DQ8G" +
            "fD/iKrodgV8u/WtigGDqF3+zB34U8N9GLww6rJkREDcKoD7WfVEmaC/efd8fxmgz+AO1euXvcBdZ" +
            "gWhci8wwlnC0QhegQ1Tm0BB3AJNvOVqaQEfLoN4YbGIDQkH5Y74N6hZvJ1KpeytBEeMwNdEctU1I" +
            "bx73A44GwBXRBYQ8wi2CSoQGQXzlNMkbxkTCQLKgkdPUohWHmLubBLg2GLLm2QnJHwEgTckR6GU0" +
            "2GfHq5tm7W4/wY9VIDhvhpBnQRqB0FIiOLD5GpiBCSBihblpSZNY6hMhN/q1AXCCWFo/xpF34NzJ" +
            "s9SQVoEZBGgb81M0k8sFAXBbYBDHkJNQMNCaro/4N6yVlty8rgzWz8tZHfMs0SHgobuVP5wKwwZk" +
            "4shQlFYrSSwiHTV8WWkZyIE0wHX0GTqiE4m9WXKQqU50rYUbxHpzlzdXA3pKJ0sYi/p9HfDOV4GE" +
            "kFID5JGCsZ0II+sP41Ti4OGCg0e6qZ+DKvveGc+LEUdgoSBu64vxueYbJy6GQSTfXazCBzN3Cr8Z" +
            "GbgEmr53YarXBmK+43cqAYK7zbo2LCW+fSDYQ1sobNBriF8gBjNYOo84n00qYTXF3VOWFHrITAu2" +
            "lr1W5XngpBf/dU2nYqiFLWtbnZoPtMdoH0yrjdnMYAa643t5ClIQW0K+41Dfdsw5Af7Wy1hmNKGU" +
            "1mRvDCUoOO+4dYCIK5zOIoS1qBHMSds5wjeD7PdUX0YZ/r4+448vy7pKUB7XGR8mjPBLcX2iXVTe" +
            "onWRqC40QmTgP9FjmAKgxCzm6ZrkyCbpqzBjRQRn2pUi0NVzykkB0QgUPAVhhHY3qNAYCMxt3ThV" +
            "a29fhrIZZ3Aiw9zvqV1HbWAG+wEmAkEk38yEayJ9WsvimZSsZPpQRgbcVc/XdB0FN80IRyLJlZfC" +
            "H4g29XArmPtk3jDluN0m1xAguChsBaN8F+Id6ESxoYkYwZQwWBjO2RAyM9cuqTJA0VTBhgBl6Ajg" +
            "hSevIuMbXRxxwQXgQ4xdTxnj+ULMqLJWY1dj6UuI0S+e+rb1BhBzgCkYLrKrYhpBTugYZYY7DpW7" +
            "uFA8LYaKJkqM8nbSKo/6RfXBwO1Z5IWfByFZXPVKqs+AIdfxpP3APi2t9yIQeRTQ/yaqGe9WoFEu" +
            "/qQ0xFLDFBUEl4nWmGe8CUMFTkAxbwRIsx4xXdnlApQxV0XI5TuTVuTdC1rfVq3wyi22N52lGwRx" +
            "nwPXBfdYu+cZXXnX28Fr87BN5z6Q7c2aGnzAj0i8AADwbisTTCEOP8evEu9kF2GZYEGCyLphWRcW" +
            "1irbe1QY99DQUnGWMkJ68PsFxUMSbQRW43Fe6AwGWvjrZT2BkGSOAOh5veAlAO1C0u1Kzbs6Tt37" +
            "A8qQxXzlKUENpElkkmBVtJgl+wRAcBMfunMj2JXWcF0op1Q0luI0ownxXblj35tLRmAf/phAe7Vk" +
            "XJLPYElJmE5GDGns+vEYPnOP9xTwb80/9y2vEZjwXcHrAeg8TVUD7G/A+gmYx/LBWKhrcvyyOozF" +
            "6c8qehPMYwO0Q/jmUGaseDA5f6gmDKXBoguuEnPna2NheDU+8hrPqjUmBoessO69X3AvWOXnw3im" +
            "lDHqTFEAMqS0Z8bCT1rNzeiY4NKVBtn+Vus6oct2ONe5UX1ZV8PfLKgXA3NYyiJwkQDYQssJlnPK" +
            "x6XI7FM9jswJZgPGJGr7MYXNVgIo2vp+yQgsDDoCG34EpHhOkqB3UByeQsJHKJvn6/yfZIxYk8+C" +
            "L/922RQWVSCs025yynAIwwflw21sl9DwarzyQYTc/XXrOeFXqRYoI+uy2I27Wyu5+DQV5h8eHD+B" +
            "CONpkuEPTcdYJd6O++2ICF7E6TJvyBR1HmG8J3LAzDYBJas0GHLcBGSEvMNV1F14VlUgwsb1mHSR" +
            "fGT5wZWPDGNgJHOQT3IqGWbAgZEPHb3+fKgNZKo1zVejW/shGoX3NbIriMYhmiPBPC8+07U5EtwF" +
            "HQEc7H6y1eijyKQRMy0CofjprbHDZTe9zRcQ/qQ2g7NdLOKuxM+JwnI9KyI3X6YX+UwAxiA7vvfL" +
            "lW8fM33xxkFiRPqRovNXcsrcyEQTYea+OWG/6JoGRpTbZIiIQtTbHR4QAfkUtEwJFrsYqS7bojTA" +
            "PWJ64mPFRBeQGrjLtzcrkw9QbbUMfAa1iZ7AwLlMdwph95UZN0AMqWPt0tiBhCpIOXdD1cNcQSWG" +
            "AkqGvIJkWx3bp0YbcF3IzPQ8F1KVqfQfp+8ErUTwJeIFFWOTDbMKAKPVKhLJFYTG02yLQWYY44Zk" +
            "QgmsC2JRMAUkacSEYYhU4ZVQQA+CY6C0b2wP5vUfMhD2+EqzlmiRft2WbIhsV7Neq0K1d979wjKP" +
            "M5gR7wAwD06YkSGYGmvS3xQlIN+8Cc4B+S3szboIRJyKlSIChNh4uAxuSYrEsSOwvrecOiVO5gsf" +
            "wuEzByJhouxnvI9fWpfiiGSYajhXI7NMBY/ixkFkC2mWZ4AadK6M4ZJbcNWO5By8AWR5xw7vdeNp" +
            "0arwupVORg9Jeoq6STZ80Ieg0MDj3y4lc+dgdX1Rvfg0XiAscSvc06VPAlOwu2jR7zllE4QCDp5v" +
            "lW5uRgd+SeHUvwEgGPTz5kGdR29JgOTHBRbmAC84ws3wWvWEUyAgPC+mlkn6Xn1izTxb/+AhrLLd" +
            "VXG9FQnOpbHnCKzLOihEhpNBZb5oFO1ZqnYO7IzKQXnj0QO4oTLL2j7HC7a/T4G6NKY/x3EF1XWB" +
            "kdsaamokNCL4eXVKN+PJRCWbHL78ud4CzFqHdNUVVzhx8XA+GjUA/mT67QcwcNYSSbJgs8VHYidr" +
            "rd/uvH/3ji/EwkGB2At9GkGMJa0vlEqu7UNdSGQ+g6dEiIxkBwBS43EhqmyoB3pcqmzoFlxADTEy" +
            "eLT6c/LZ6buwCa9WAL2Eg1ln7VtPhwdTZaDOZURAYJOH+WC9WdItsdouQSoRljFeYJPLTjNlbL9F" +
            "azsgR7MslToOG2KKjqrktFHJvZ0aHbOuXS5lonasM16FO8YUpWPGX0CkuWIj+5g7yBRYCATCEYGs" +
            "U52PWGRS7pkGgw3VEsH4kAtPRUzmU90EQJ31mc5Cmh6jxyZ0vGgOmWq27HK/I3iDQ9byiIPigupl" +
            "V4Dl1Vjuh4dIiDwYAsuNOukvvNYs6vcN9XyghI7Z3A2IIo1eq/D9xl1fLljxNM+HLkfkKSwJdrTJ" +
            "U/BrL3kG+xUJUt0YrGPYJmVDW0G7M6IV71Tei5HQm6OF7pd3okeqPVANOirrIxB/oUGezv3CMXyb" +
            "bjosyBHVs7aOe7VwvfyL/LG9BHVQMQ+Qi272S659Zp8G8sXiRfvoXojNXiDeRl49olS27EiuIfGC" +
            "PSWwv8WxBTThDXmrXQqgKsii+VeLTWZuecE1+Tweag2S+hSy4mXTV4aqi0WdMOw1q8hUHtKydX9a" +
            "QazbrwUBMo78SURgKriLzjM3+2ytOQBs33xeQI7M7yQJSQ3f2vlAYt1bi0T+46nQCsa6i1+gOCj2" +
            "oAYIGGAZlzCv4Ho4b++u24AY+CYSDGgjfDpRiEYGcl1SmoHc5NvJ2qBcfl7gQSwENGfDQnQbFYrM" +
            "POxF4ll5B3PQsEIM36PV78RCs7iRwPhBsoBXqKRo7RZNlExaJCYXhB6UjXAyCeRiZ7E0GPAd+EX8" +
            "xt7VloL3ub2qy/HKp9Zdebmtl14VKaMsn19DdIzI5I+YsLt9/Ei5N9J52AuDs8Bqr++8jtlGwvC+" +
            "tfn2Pi+s0O0zQY7Y4YWouW2Bq6f35QjeqlqZNkmk6RzvATgXW3AIOokP0beKBLkaLqyfhhlRMzdg" +
            "kCQvelF7vjbvmEGpBdvanYFnGs/+zOp2hDuswvTf+lyXYC15YCta3HCIBVQyPTkEjRAm3Wypc11Q" +
            "EU6oP9nGvqTMJJqv09BHdix5aJ2mx9MPtgP3oEyxu3nKtcI2Tzh214gDTohLKMf2lbQR7DylViYZ" +
            "1/bvZGwhBjLdqKt8qqfMYkhfQGTZ/gC0L1vqIrDLDbvoYnfg1U9+ByZm5oGrxdtbX67W2l4lTuuI" +
            "GGz4+g5lW9aPlpXklOwav1qhxqsgo+NZPuofvlF0rRgkq5qTgUN8yLUwekmfy+3JhdyCxiJiY7G9" +
            "yxXJ+F66UAIoKRxAJbtpXp1oPpVDohwrrK1mAKomEnlReSzUBXi7Sar71QW0i/ywmQHSWXuUYn1d" +
            "U9KxvskiiB2+AAxBAJfZGWnp1TZQbMbaDcnSzxrYdVbNCFyCl/gh7YEI5irc+0O83YMhQW7Es1xI" +
            "QIIggIPazBUNRqWgOCZ5d6EUgE5I1eqhbQrT0hvxz1OervKzgoA0eSo6Fb4Yx/I70eMpVtpIUB78" +
            "crXJtkYcHqQsxRrdDDPASnJN59Q2c65MNGNcrLY/LjJBt/iOD2lvd8GFNp62mX5JuxOJBuJ8yauW" +
            "wWx6wUr0s5iN4c4oApcc7SRYgD+RBjAR+c+18N9C+7L+PHXBIHKzd4arL8sGkaB0uwFf7irGAmdc" +
            "Xsb2IY2gpyAmx4s5UwxOnad9R8ky1P1OlIE1SfzmstcSXUdWW5W1Yzc79i8Sber0p42Rl0IEtFe/" +
            "F3AVHp0YTteIifzx2FoVBLZMugNApsk3CZTXai0aCVcYQi/xctk0BTsk+nCVEj3JHXLX4YXmoDXE" +
            "Cn6iWVf/3gbkJTuO7CVGDkE7fhDZwRjp5W1I1QiC39OFjRuDSB6kHEFBZHWorlZUxAwU9DzQgHEN" +
            "HkErzHTP4cpH31QXTSwft8FVcQ6DxHbpINnT2qRXYLTgTEihYB+vXifLSShwq+nXwH2jDsf8bgwp" +
            "wQ4ARnKYNyVd4336xzYpNZ+TFUZTfOxJHC6lnuV27OjV/BfPP7oLP2BBtc8WLHutpDKX9tvywukL" +
            "5iYMQvHkJowBkfkgly2/YI6tlFaoAhrCwKEb8QTCENrcprmY3oGRsbH3Rd5NwOF7bn7fTxfF9OWg" +
            "16UytlmsqTOyzdDRoHSN/2Z+2llHwGVgC7O1K+7QBgE0J8RTmpkOfI91wfBkQnORMNgZI2CTRWBM" +
            "tjJ7pz15oqKKRgzhGnKaQF/Qz/QsjBAPRNkFDT7nyxHFeP6O6WmOL9FFmnXoPVa7mpCBwNgDiMx7" +
            "bJvL/HZkHR4euN8Xcc83xv7y2K5FEb1L3NnvMc2YDpJo2Aey7N1rtuCDHi/sh4KYGDvy5Yv9Qo4/" +
            "N4KPfCjaw4+Av90SIhbZnrt/E20bM5brMzGI1XyaVSrqTXnGGF6dTAAskc3N7pX3Pt08EzpkyONp" +
            "CxcxXMlC6iwuNpmbfZbRPvBnKMh5XhASe1f44obWffXaUGlCzlhMkhlsErO1drm9xor2wDjrLMCv" +
            "U8xfgDtQmzJ0GVzPZoLsmvaeMcrYIyMZqrOIgpqtoha+LjzVpb4X3Q2KEmjQAUYOgmQqxovnzvpd" +
            "G1PtsR6okuC6NDo7mZ4OeppQUbGRC5XiKoSV9kVSwmTo7G6h0EKxZSVIsgL4FbBu2VVWQp2XLeNn" +
            "xB6pVWyHzhbxMcIuB6OuIYILplCJgzilEsL5y69DKy1no6eUV2WnWDHXqgjBnLynyIYQye3UKYgj" +
            "gKCeTUqEekf/HXZNMRHcAbft7NqJDARhUdo23NsA/92J4DoQkosERGdjzwWHziCqYwpTyTfqqAk7" +
            "UAcQ4Uld7Qv2DO2zPEl03afv3Fpe5Y8P7o/uZ0BK2yhM2kbbuGrSGNnElWGWbhsUuRaSy9YAK54f" +
            "YpW5UjodsguEdNo/nxspUex21VntEuZZIjf3Le9I/V8g/HAm8+k2yfRs1ZeBIAw6KQIbRJecZ7Vf" +
            "6nmX3dioKVO7nrXZB81uB767FnRXkQgL7rsiTmySx2eARxEZj99DmxNo4Lo5p1/G+X5v4Zn59g+c" +
            "XA186JYsEsgSitBbbPiyypcfNCQP0Q0uJKV4CssTNGVLLHY213t8T55aS2kI9CSuvkrKYRfPthDG" +
            "kT8u7QkyKPYTvDacnHZ54K3Zj7Ey9DXtsnPRGh1rP7RtjGtoihgOgsv+0MueWDeJ8ID8jmVf6Fpg" +
            "ye9uNhoVXsAVjmijsPXffTYm2Kaqb2l2cKHBLiANG7+xWsIR5mGdRc9gUzTeJbrSDFO7/2S47G5N" +
            "h9gkH2B1UfW2eazA/duORtitW5Rf2cobMimfLQ7TcLD8r4Jpbnuw+31hyTAbxJBEiGNtwOJlN71r" +
            "rSste1hxbKhXu0TAhhtNOo2nJ7nfEg9EFHt1Qvf0BJTmDo+CqoJF3EZDBj5BUVmiuHpnV0SZQWbN" +
            "nVUWMwFaFARGAuLAIeWfQ+KNn3QSuRAjSJjs11C1TcN6hCZjGXToTxut3CJii4dtwkyppQpQzEpb" +
            "RRG7CpmvG9JH6oUioUOVrwvL38c0ZRcArAZnntlmDVCd6eEnjFhzSRQyGgrEUO92HSmcRDILYGWc" +
            "1gFL5IjujaNBvk3XEmDR5zPmSCW3jQyb3le192XZQXEhB08BT9AMLgxUNWXjNhHMDL01wO6y971e" +
            "W0ukvs+u/26j4lHv0aWQS3fNd1otGU0HCpHXjjpEdVXGHx5BWN58EOGJTQZ+QJHTy5ZP95a7QbC3" +
            "192cpnh6BsJ8MHuxG7mLuf4QzakgS783wg/5dLD23YBrUKgj04kE9Xyq+Sr3tnqdQCWHJg4HAsyw" +
            "5Smo4E+aFvsMj5coRT2CvmOsFgPM1IOS+b2Ca/S3a9ng74fU0Z51VR4IoBds68O5doaB7JqG2x62" +
            "cVfVh52A4yZnwhWgUrADlWw/ILKraLQn1x3GiXsNcFiLsJv2W0EL3BpGv6JMRc7izi30/ZVenBTh" +
            "ieQYRiPUOIb+RKFkI1NvrvbyFKfeA2u4i6AOnWKrgJ/4yFsvwiN3BuQfZQ4XJPnZYPbdKfe4xQDp" +
            "Px/bS5B15KiFVyKiIJUBra3kTveV3X6jx2pu5AgqRqa3kgBn/ZucQOLCvXt/6yzdMKip99PTdFbN" +
            "9nZn277ciley6xvme0dHwqhkI2Lgs7TPPCJzGUHS1RZ/8CBbIIMcht6doQoBEL4aFFVP8fJDiqHX" +
            "Z3G/C9iDdktuNXhgOy0dhgP22G67wa1hZ9Zrq5faGu9zWXn/GjNeCaPi5qHXLmaIiKlnljLTQ3CB" +
            "qW4Ov1HjMCxxa/TY+gkXFl3gZZefxZTtEjQotKxv2CTzGp7hNAUlqJPsSvDUcvXirdaBxqkYwFyM" +
            "GUILS15JzQcrY92FS9kE1460G1ZCUAC4TMhumTvNDMHSreb+SUTWebg8xwUE2OiDS5nPZ5RHZzBb" +
            "4at28ULn2I+HgLyxme6tI7mx8oAv34IVr99pmbnm6JbIKjDaSQL8d8Zd9Bdx3nF4p+GwfgNyeV5b" +
            "pVt0N9dZ5US3MBrTKmYtFxEyjCDyxj5nrv240u6O8+ieFpQg8ioVN8BUDbmlL8SEcvdPz4ksjsuO" +
            "ZbiZ9HVJsbks3uUcBszOOcbplAPsBwWJH+/1cRdTINbdUnPb1tMhtKtjNX6tqjb9yVYD9RG6FbW0" +
            "zB47nz6p+PX5mMsA9wcEmDuv3V3m5oZy9Q6IY6Uf3HTDCqInNI3lFN4x9cr4hgb5tQpve2t4vuCG" +
            "HyRoO1UBiMLBru4sCUoH9+vW8otG25pzD64U28hAatnpBOXYtY5otvuqJTISoRiHHQgw7Dvd8dkS" +
            "n2rWisf6rG2W4W4ZUIlhnjZ734AVegUYQeridZZ4nG5LzdneGjsFbT5yZw7u82nA8+suJ+wLtgyZ" +
            "1XECiQDHSCJnzsKORZy5Yce+e2VYcNlgFyo0uOqKnA1W5MTZ00QffZjn7Bsrt5voktoA2RHd+GMp" +
            "lERg3NtzQQWoBgUu8Y6+nKG5p8cl96U8Qj4i11wYgrwROEJUzxbLICzA+n7uovm/AGlbvbYAiw+y" +
            "Lz1s97e5LxYx67riUcku7mMHbQHHH8FeUOENujz6LnR2PpWbSbY/8152hNh0U4Nu1RUSCNj97o0H" +
            "Y9qtxQE75vXmaoQsDqXawGRJgwCGdvFYww3OLnbY84RXTuRNMrSQKq6AnsICtMz9wiBwxoxu9Nzu" +
            "2r1CrI/Ni2Rs/dwd4K5c6dPatxUsZadt+55fAOyRAuO03uNZgf5qOLjmd9XowjLu+Vkv7NUQUWQU" +
            "jyBlYe7yIZOaHkcYNYTC0SlaWbROaLnpc6/sZavATL8dOe6INq7c/AI0IwEZpvPcSPceCtiSkNxI" +
            "VBfr3ZpAdAEH3NV7AWUu6e7PTW9AiLtW3PbxcJmBXYeBss3nwZVNQhm4fpbQDAUXfDDpYrf9c72e" +
            "0XF32aKTuMG2RO55N9s87SdNwUUJbnZXGzsQdKO5Xygtl2vSOn3aULa7sBM+f+guEDWCxIgWSJOH" +
            "lmAVufX0s0n2DAwGKaEb4txIv/dV0u4P6eeihttj3UWKmlBvR+0LxqS4bodPP52k9vwhS/6QgidU" +
            "8BKpEu1e4QNALajS2gODos1e9yoGbwG0tvJCOtuCt0/jGbyz34TIcWPAdLcnKuJ0lZ9un3z6rIfu" +
            "msSG7+x/8o3ujHKxKnqACYwW0GP7RI0jTQDPkLIJgq1Zlw0dBUOj30VPMzXHC6FlXWnGHBF0qcAe" +
            "YS13W9mJkYicDekWrJzNI8orgO0TVt3ObK07W47RXXdIh7xVOewb1iRW6qPoQsR86NXkgjyMYbzo" +
            "3K8S3T5pw+1Tc3yJ3cmE2nHUCmEIfGhQt3ucEIUBJctDQiyJb0iQyXa9DgK87B46r/c07b14rKui" +
            "C0iPF6Ky8RnLbgfvtAtIBLTBcLqZE/sFqOCeYYvL5dc20bWtuTOTUXABiCsTJfYOuYQef4sr5PF2" +
            "Twg2cg0XmOzoxNHyxLYMazgGHw3YWmQGSf8xqujH1z3u9R1WOex/Z8TdbbdcWY5uQqtnH/qyXPPd" +
            "F2OVPQ3jxnm677PAsdhqbtdGSqCgxFPe5xOAHIjfibrIRFjpD2dHE1867usm3pkQCyrKthspzSfi" +
            "m0m/5zSkRC7tF9w98WkEGfNK0CA2LI4JX/aSXfrSzPdMLTl4BNa89iDARqfZpsb0wwtXyBFFkA/0" +
            "M4UJYqD+9jlg9a+6RsxuE0bMJee5AozxyF/LVkBvep/7tJK6ogzaBmkEkEB+3dFFQUDsnle83Rz5" +
            "ts/94M2eRT5L0K7D63VbFYKLQ4bTV/65tWYL4sNjzS3/3/bvXjwmvkh13d1RpvYJruji4tOqbhDR" +
            "lnaskecFAX0a3fpbm8ihWNbmtjcI2a1DnI4AItUWrbOsJsoDOa8t6ktcfXhQUKYUAnO54d89NaTX" +
            "QiUAl+vipj2ah8DDR5BiWJIRAoTtaUsRcrddBI84gts23IMIBUcPPcIs4cM8E2ffLV8u65P16qvo" +
            "Dqmom1eqWYRRuD7vLbn6MAj9hiFDVYfTKIV0+a004iJcgn7sHUloGNwKefJEGzus6PCQ3QL1LC5N" +
            "FBe/bdR0GQ65BB4wyDayw9Zk/+PG7lIk3mQziKWK5uENFgGSfWHLMpbEaMhbDXf3anE1e/6A19Z6" +
            "eW24l0M7uTz5BDllo7v20Y15sOmD2UMxTgR4L91gfSxq2Z7vTqLtkkW90FB8bbUd0bNTjn91ykNH" +
            "Rr0A1A2rKPunHVy/nYeIe8KSMfsIwtc26NAu7MZtHyHXRzAF11fraaK+mcv81HHfPAnW/BwDkBBT" +
            "fhkSrVY39dnkMj0f4GLgA3RnZ113jc8yeoFpF4TrluV8N8JFDWvXJo/HUK5TYgFf2msrQLZIdWG1" +
            "EEzbnhlyFOAd2QLm6YctIM4AmJihaDd7SvGzA9s19EwKQpGM4HjcKH+Uv2tG230l7pNPbo3CKmGn" +
            "7K9xcxLmhfRobu1vufcHZCDPQGP8pe2/DS2EYnvC5poExbYobj94Bo49U8qm1rBMle8ML88dbNW1" +
            "xWueOr37bpk77OCFz3SzCNmTQnP3PUYCu4/ix/qTl9kdbO6ewy26Kavhtl6/Tsk8A8Yi20yAqtVK" +
            "6OojoIZRJHYm5FHm3VWxbvzm5sAS91m5mOSpKzyq/TZa9qGx6SldGI48GV1sEDAyEORIO3Ju2pH7" +
            "eDKKMDNS9UgFG8IZydJsGxerrVzZPbP2hUJB5sfqt3XdAFPSCRxGiREGwx/3uScLhvVzQ+VaduBi" +
            "mhNM27b4bJPEdQiC/MY7MrYWLtayAMoj7eHekGVZBcG77Y+yPod9bmV4zEbWq7msD6Nd3c3RCuXH" +
            "DqTXYyK+6ekluM3qMSSvRSs8qPsXyNiuv8epoupDqaA65o3U6BdOJDxnUxgQyjXd4rmJTSbfNfsR" +
            "4pnlJF156MeygdRmXYsthhl6DfdrG2N36JR/O9guss+CTYr1FJFQBHjaZLOpO7qAYJTiR37KlCUA" +
            "XYqhDtPWs22J59S7YAi+reECK5jlZc9gFzqrXaIefkPYIi0RVCW4rdj9q55j8j0XOgDfbJ3PvigC" +
            "8uGBbZDOtnGQcri8YeUxoVCwk+fIHpvDRPnv7L4E1hERr12I2SF009KUh0VeRjKFjynTyp8zZxRH" +
            "GbCu5AlRPqrlBO6ZC+ztMSFiM/xJFDHXHY2CvU/RNelasT/w7vYgjIG4eWxeW5+NDzwTMbbd5JI/" +
            "t+Neqp4aQSP33mDqu+3MRAvjjKZF00VXzHA9KBPyEFxI3e22tjlLsZ7twfwTkG4A7B7VRNrCB8u+" +
            "AfDFhtokfqgM5VQ37A/LKNCrO+wB/7OuhI5EaZIiOBz7nM5WE5eIcTC2QSc0DtbROO/WBAHG7oE9" +
            "lrltMXZTTz2dwCACmumy+Runl4e1eJvIc+X+2mfz9CrTQx5et3hMstxd+2KDq36MIWxZUAwI2VrJ" +
            "/o6TxkQBV+C7KhKJCnrHF2OcmfqqubhdjCPuXURbG86DDJAf5+Cr3Txk6Ep1kgkIlsct3uDPOrB9" +
            "9uObcO7zQE+VYsu4+6vQD9VePbLWrSOunD1WIpbptF1S8ASQCsOguMjJipCIUF5K6uJoAcZuvhgq" +
            "3nZ5BkHy7J6Oxk83838tN+uBnh5fEst3P/Z0rl9TUbLvvLsh23Ofgq3vL7HIFKIPiY9oW2r3HKwu" +
            "QgrM0+1r+LcPS4Lud4/rY+9CLHf9dJKFa2wLiNrxDlZlOS27Dj8UXc+Vzt6LmjKceg5B8XQZ5RPo" +
            "L2gzcr/9hmdLWnF1Wg+2lh1uS5kKMRfPPoTgHyMRZ1PNChdFYMNXQydCoUAJ9VU1otjN9No3C/9/" +
            "NgLxugcTFWQN8xzynsMDiZKnUUCiyPD6kfYDLBCeBlz86wOYe1iFHm4ia5YJRM/jtxDsQZAaLpIP" +
            "O1OIXDUjxPcmZtSOKc/zcgMCgXy7pdCNVTwVoermmpsMe97LLgBXHr0nW1DGrcheN7nl9qGEACyV" +
            "++QLl13Uo22rgS5g87UECNjNn/P6oNrBbwNBfJYPTX0l9Jy3XajQiVv7hhsjbvuqK2b7Trh2e95W" +
            "OcdJpRAvaHTbMHFW2zaT190mXUwWxj+4lqUQxrcAJMQ7qWyVp7kcfvaYfW7pSOMqNm7Zp2JjNorL" +
            "Bqn3J8qG23agANvtwck0Fo+uZkRbbI8aSSSAJtPtHpdLL8kma3f/IkvcbcyU2i3Eg8iHwHf3WJwn" +
            "1HedY0Luc8bW2afRLcm7A+5aVmHOAsPt4gecmM85aSnt8LoJcbp/0U64hwHvjELdp12bMEVoDU8f" +
            "s2/q0g66VptRbAyTrt4uhM+F/rKbhS9UNpdS+RfgpJ4sIbJRv/k+nYh4pnaBs+G2k80ewuA5qK89" +
            "P8VDzwTxXmxXc8WVOR/hbBjqy8bqxxpwNta5g+/idQX7e3alDztgY7b1D4i1ydulnpHtXMA2AaKe" +
            "Lhe5jIeuhceKJpPEt36XtOsZf1At1guBtu0wecGsmN3KLi4Te2Tycsup213u3yYMMiRYROV9wYMC" +
            "GlP8WL62OX4/52iw6L5aS7TuoD+nMCDHXJxzsWN5ogUz62Y+89Se0PRexcozninYs+HuG1tZ3KUN" +
            "4j/2TZOvzVF7N+88DaUexBViLIbAOVrQ27/e9HiMXxLA7F3a9jpXoiejWxhpJflZ0IwIOVQV/v0L" +
            "zVo3yrb63mKzbLuGRy5p7I/TQaB4oM85s0TA2KBl/tZECtpBNut4NjDj7pSKC7atwu5X5C6yxuKl" +
            "dUfdT33OsYJICD3Y47lP01r0cv9SOwfgvO4z8AgKcM1duNl+35Iuy8zpMBskHM55ZCo5LPki2t3Y" +
            "kT3B5AMvkNDE+rLrZdpO1s6eQd7OxNXLE+ZcWVc3JTu0avBQC0gIbTW6Wzhs2htk+9kdZT2CUdN+" +
            "nO05Vn0yYHtxCeSDzYxuyk/v/WUFM24SQ+MxOm7Uuj1abLkof3arH/5pZ/3/uAFVxWVaF7z5iq9F" +
            "I1vxYAm+3VYMm2Y30OlxaGQCFr/gelxKyUiA97dLuFYXwC88QHYrzMBFfS7Q4yt4xzk4V7YHqtCn" +
            "eJim9/767Z4Z9+liixjL5TlELsRc2C5ySwfv8optP8Q2kWvneVvZwz7tPnGXRPjG45Ols00ZhY30" +
            "se/jtgB0eZoMyYhVrCAQ6tKtf2jZb9rX9dsEUJ5EVD22UhHh9opvjxGykOtdd9C1XmpuMNg9Dfhf" +
            "xAFMrTpi5E9j4LB5hGf5XpQDail4+gHgMkkTt2K97iTGt16vLeaC85Jdd1B7OtPp0RuhUO3RB/Xu" +
            "UhUlp4CXXdEaWAMP4JHlkocEDbvVm2/GiUQPo0QEfQkrOw/FMW8geT+ns8nJng7s2aaE+oPuD24z" +
            "sGsMvAP6PNG02CZ5T9UvYAzpwM/wDCA9iIN3/zYP2HeNyuarAJZPHh/npIZreYwMwe9Cc2BGs4eL" +
            "/GM3iyc2PnanffHXxIzgAYWBYE/kgmBfPI8t6J7IRPYxbu5dRp6chthyA3ogeT1nRdi3cZ/jZ5Dr" +
            "UEC7rT1m+1GnAWtl7077ssWledk3ofU8QNTqybuY3+YsfNMVYYfOWHCJ3CJFsb97wPVAYvKolHWl" +
            "Z3uKJ9SOko/46+cgh3tKFQrj10mHMnLrOVB0Vmut255dipkBisN9BdfZAsF97KgeAKeGCzLxnEPx" +
            "niPiYKHoWY+pu2CVzykkn2Utmz+klihKXR+3a1X18ag6l4+CO/4bWu1GSmRPO3UzdEewvPd+2zoH" +
            "e3Ub3TBt5VbsYPHey3MZGfs5PKy1HmTf20YFPlvPRiJ3x3gcAINR3ip9eNzEOUjCUmQ+bcX1chPB" +
            "tG0P8fVY/8kWkXDjtpETDelseEhRprFvztYebAb3aVNVf0f1CIi5r+gxklEEcBGW2/x06ilveR7J" +
            "4KC+ri4ZQcTgWNaV0gnbkBhY7mCjPa7seRCk32urJMod6t5qyhA9Sq8Rrh5Tsl11Vl+5IdpyRcyu" +
            "3oEFCC3gKCtr3AZv469LOrOcY5N/R4a4nMZcA2I8o43kxLCrRmvYlUiqZjyD5w2B8GQ/LuX5jJhq" +
            "17CnAf9hTyIyQdPrQ4E3gvit5yxGCF7XQVben7ljKQLfcUFgi8lkOL9J1r0gxQxYl3FmDNXtGU4f" +
            "VAgzDnspuJ4LyzhT2zbQNXrn+l1tee6WqU7wRPe34Iy7p4C+ciXmqGspsKLjdhVXRZzV4/F5ldLv" +
            "W12mHxfp6LZUVxVejwK0iZ3JfzyVwv1nZOmwsoQI8MA7e749pni4iI79X+ecEDD9AolRV+Ic41Rq" +
            "jGdv7SwuTHgk3+/UPs9JlYBxZjatbiyuS5Hk8Pe+bv/tFxLcvfxk5bLZjWdgohkxi1KaMYbO3T3D" +
            "1ltgZZwjS7iJhD6pNiDZggSfX/v0KvU/216Ua3sJXS5S49KhWveSH9eAAQcugzYk+HTF2XMPVXq+" +
            "cFmy6MTbDV+8H7Fz17NDAPoijNZnEwxRxzy6VSh6wCRGxC6/7okCTOrnEli+0EnJ4zzGV6aN7slF" +
            "RVccwEVPP/V0rYxYlK6Bef5RoC5FmO4WBJ02y9Z+9eg5tcwUlmMgwZltKwWB4CeMPFxW8/R4gs9y" +
            "K7X9xi6rzsedfWgpvj2BIBeaytZ0UORUX4jbZgujpwmTWj2cBVsPijybPwA39+7zKME1Kjd9utqv" +
            "hWgeyH+f4zIq17PTxb0VzRJYvz1Q4iwNemyI/f9Es6deerYkPOjR4dsmq56vpJOw7LLtoH9JL/fU" +
            "AUcwxfIcz4KiXI6Hexqm1t5WP0ineLij7Ux2e5VLl4y1c8vCTlHln3Z3KwcA7iHTze+zXvd46CRp" +
            "4DoeaHL9L2ZzSuR6Vt8dAAAABmJLR0QA/wD/AP+gvaeTAAAACXBIWXMAAC4jAAAuIwF4pT92AAAA" +
            "B3RJTUUH4wIBAQswWpd02QAAIABJREFUeNqcu8muZUmWnveZbdv92ae999zGr18Pj/DoMiIjMrNa" +
            "FluIhAaiHkHUCwiQBgIESANNNBI0EvgsggYCBEqkhKoiK7Oyj8Y93OO63/b0u2+s0cAjK1ksslgl" +
            "A9YTfLaWrf9ftoRzjr/t6bpOXF1deVrrpOvaizwvfrharf7o9u7mB/d3t48f7u8nu+06LPZb1VaF" +
            "XOW56KqSZyc+me+4KwYCIfDCiN2h4A/fzyg7Rxwpzo4f86e/uKazPbORou07Pv/4fZLJiNvrB45P" +
            "ZnjGUuxyZidLNtuG5UzSaMeRzfGTlOcbwfj4kp/+6ldcZIYoHTHNEqqiYDkS/B8/XREG8PufnnK/" +
            "zbmYxfz0ixWeHxIdLQi7nJNZzB//6oZlFrGcJKzzgdnY4rmBWRbRhu9w8+olne058j3Wg0WKgMez" +
            "ljAZc3tXkZeaj96dEwSSy7MTCqcZjcf82S+3fPjR97G6ILIN5faG+dk73K9X/OmPv+bDswTrSTbr" +
            "ktPTgN3DFn8Y0FZycnyERnFydsKmGFD8/zjb7VZYa5TWelLXzdPtbvuj6+s3P3r16uW7b65fj3er" +
            "B3+/30nbVSKQhkXqo/2Y++2W0nc4J5keZyRhwCgYsZyNcdWAM9ANhkF4jCQcTWOm83MaGXHzZsVx" +
            "5BM4waYcODlaEKURzgZYcpzy6LuQ8+MApyIab+B0kbIMDkzPx9ytCtp8Q3h8jh1qPr6Y0ZYdy1FG" +
            "XVsA8qIiVBYXR/zrL29RniGMRzzs9jRtxSJb4vk+h1rx4otf0nUDy/ML1vUNg4Zl1hHEYyIp0aZm" +
            "HCY8milqnZAfKj74+/+Q51+94NHCw5kV09EY0zV46ZSqa/BMz3ySopXhZncgkj5F0ZKlAadZhHUe" +
            "eS+IIx/d1xwOFfJvC68sS1GWpTTGxsPQH1dV9f5mvf7e9fWby6vXrya3b14FqX3wTLcReZEz6Jpx" +
            "aEkCH20VfW/JkpRHkxFxajlbRHRa0nWw2zsaMaBly+OZYDyboXyf65sHRDOAZ0myCBtAfjgwSQQX" +
            "Uw8VKWSY0PWOOAmxtmEa9HhdyXQywmnNQ95yOknY7mqmvmAxCXlzfc++bfjxl1ccH894Z5HRGsft" +
            "zYrHRxNUENPVFX3f4Sc+325Lnt+3/OLqjlc3KzrXU+y/RdEzCgxhHOBpi2c6rIUsjnAiYOhKjN5z" +
            "+/UX6OpAkkYM5cDq/hYXKdJRysPtBi8IePZkCU4itKRpHE2jOc4y/CiiFz4yiAh8h/IknW3/9hm4" +
            "2WyEMSaw1mZt213k+eH91Xr1+P7hbrxeP/im2sk+bhGmx0eTKJ80DnGu573HMX3l4/uO1nZMjo/w" +
            "rc/DocZTIcSCouyZKsXy0YJ957Hdtxxlil1lGMcxda24uyn4wUXI/nDA1wIjJP1g8cTAQ9Gigdc3" +
            "DwzW8GrXczwNCQ9r5mcn/Mvn90iv5Xq9IU1jvnr9gBd4VPsSTyhe3+/50dMlm9YwVLC1Oa0TVJsB" +
            "NwzMEkfTtyShh+1hSDxK45BWUG0Kdsmc+9sHtrXmRx+M2PcDvYOQAFOVSDys9si3e8aziOurNR8+" +
            "+Qg/2lEUORgYjCBNQtJpyGpbUBpLsZPUUhELQRR7hHGE7ru/HcDVaiW7rlPW2qTv+9OyLN/fbrfP" +
            "1qvV0W6zDsvDTsykJsQyUoYw9YnjACk9Tk+PydSO51cdcejR1Ja4MZyfZlStYbuvcEIw1JYn7ywp" +
            "Bsn8/DFXP/8l788iukEwykJu7jXWRgSjMW92PUPbslhMqfI9aRCyrzKGrmDf9YihZjKZ8uLVLZfL" +
            "jH/zzQ2bPXR1RzqZkIY9Q9EQpGPe7CuME0TK5+vrA6EvyHxJ1w+YQTIODRNp+N7FnIdW4XqLlQFX" +
            "m5bbypDFgkEIVvsrEimI0pAsjhGdw8cgg4hDkZPNMszg6GzNZDqh/Lbhen2HEQmBbMjbnFhatlWL" +
            "7iXHs5h9rVEupNQtKpaUvYevDcfz7G9eQruuE5vNxtNax33fH7dN9Wy/333y8PBwuVrdZ1W+98xQ" +
            "i95ZPCm4WCRoqxGBgKEnP+ToVoBtyMYT/HCM53qE7WjqDmkHPNMxilOkdUQjxSiLWMSKvHSMkphv" +
            "rzdE0YFJKjjUPvfXW7xozH5/IDI5jy5n1K1B9y27XU7RWn75i2sS29J2NYcuoK41x8tLImuwOqMf" +
            "fIq642FXEnkh+0OPRXG3aenqnNPZmItFwrOzKZ98dM7s7IijScqPPnnKk6MJf/fTcz69HPF0oVAY" +
            "Zn7Po2XKKIG8aRDSYa0lHXvkZY7AcKj2dENLXjaEE4/DtuB4BNPFDC1CvEAxnyrGkSRNI5yVbA4V" +
            "81QhlKGTgqquiZPob5aBdV2Lu7s7aa2NhmFY1HX9bLvb/eDh4eHju9vb483qIWjKUiwSj0Ba4lRR" +
            "1R3S9/Gc4CgbcXu/ovVCtFPc3u84Pjvhcu4RRgn7asXZJObb+z2PTwN8WoRNuHrxLedzn+crzbOx" +
            "RYuArpf4vmXbVlycJqQjSdjAdHFC3WhWm4rQdXQtDHqgqGs+uTjialNxlGX47cDTkwi05F/+6oaT" +
            "ScTNruPJMqN0gqqsOJ3AR3NLS8DFfIxwkq7OuTie0HaOzy6nCGkI5gF4PsvxKVVdM083xMGIm03P" +
            "t/uB17dbRr4lCgSem+D7IXWnqQbHYeeYjUqWJ2d05R4Q5KUlUgG+aZmOR/SlJkARhAHjkSAJAloj" +
            "AHizK3nnbPE3y8DVaiW6rgu11rOmaZ7t97sf3d7e/fDm+s3l3c2b0ep+5TXVQWB7Li/OMfi8XmlG" +
            "cYjqNVDw+DSgM6CkJIp84uHA4xOfbdHRdR4GzdlizKeXivksYTGNUEOOOF9yOhUMiU9ZQyQGhBuI" +
            "pWU2XyCFZe8km0rz8xcbrIVvH2q2ZYXpLO+NI1oreHJxwqu7HUeZZBLBr98U9LUh7x3T6QhtBKou" +
            "+ewi4Hzqk0QeR1nMaRYwUnAx9aEZeHwcM5tnTGdTppOQ46OEi7OMs5njn/y9z/m9H37Mx5cz3l8G" +
            "HNHQFBalRgw6AROiq5JxPGLoJPt8y26/QQpN0w7U2y2PZgFDYzCdJUx8aq1ZVwPTRcLu0BJ4PsUB" +
            "+lbSt91/HOBmsxFt2/rG6KzrusvD4fCD+/u737u6unr/9bffzh7u7vzdbisGUxMlEYeiZbs3dNri" +
            "e4blImUSdVycRhSNJVKS5Vzx/ScxZddTNIZHi4iz4wmJb6hMyPVDzaHtWS4XhC7lKDU8zib0rSEN" +
            "JLoDJRuS2CNwHSPl8/qmJJWS53drlNnx7qMxxdAzmsQcj5e8fGhJsJS258urFV+8uKfsDNN4TDh4" +
            "HImGz56MyZIRR7HHu48ueTrzSKTl8emY+SxieR4zOprSEzGoCD9J8NIJIkyYHJ2hzUDTt5yfZnz/" +
            "2Sk//OgYaSvut3vu12vGSYiUIMIOoaBpIRvFGN2jBw+DoygbWkI8z0eFikPX0Laa1mjKQbCtB67X" +
            "JeNpTIP6j5fQ/X4vrbWh1vqoLMsP1+vVj16/uf7w229fHd1evwmEPshY9UhrwFrWuz19N+CsRXQd" +
            "ZZsSxyFlr6jcwPkk5tE8xCQnXO9znHWcPTpBCJjP4PahYbfLGQeG7NkzvrzfcZpl3OcVzlfM52Ps" +
            "4ozk8Io4UDRS4cWCq9sV52cnJG3F2eWUvrHEKsWgeOh7fvbLa373gxPacsfFScgkTjBGY3XOQgx8" +
            "eDnCxMcoKzhbjlFmIFAefgxxrDDBgniS0ZQ9BugAFU2RzkMYgxU+xrZ4cQrCZxEFVKVi0SpuNyX1" +
            "oSX0DihPkBrBSQaGkF4bIl8SZYpt1dK0KaieYBQyjhRdrkinHqY3zFMg8RluNULG7Mv+rwe43W7F" +
            "MAyeMSbuuv4sL/IPHx4enl2/eX18e30dFft7+XgusL2irQf6pscIxyi2HI0kjxcJtXHgn3LY1pxO" +
            "Y5RIsFpS5ANhbOh2lqN5xiTJ+cnXPY9mls/eG6M9x3pV0m9fM12+yzfrnLN5isXw6psrPpxb1uuS" +
            "xTgk7zVOwu1dwfefpORFR4Xg/XOFpxS6KEnjiIftmuNY8OJ2jxlqfvejE1rtWM5Tzk/OWG12yERw" +
            "Ng0YhT54AcaLWCwueShL9oOiGSR5b7h+2FL1Ftf2SOnhS0EQSEZxyukiYDJOSb2Ui7BHRFvyw4CV" +
            "HtPJBNdYksjSGsXYtdwYxVmcUI8uSMyO0RBRdx3a94kjhe/3bGvDOIJOwiAt6/UOz+OvB1hVlbDW" +
            "BsaYSdM0F4f9/t3VerVcrx6iw3YjF2Ev3GAZeojTBE94tE1OOgvx/ZS8HiAMqPsWJzrGyuD7AWVv" +
            "mYiK2XHIq9cF93fP2agIL1hS1Pc8ejrl51/tOV0MPDme0NU9owhM0LHftTzcFvzgZMG2txRacrfa" +
            "cDY/x5UrFvP3+enLnxJhscsFT8eCupckfo2HYF8HaAOzUcjHT84omoZpqpB+wDwL0MAsrkH5yGjO" +
            "4Pnsuh27PuDqtuD2fs/Nesd6nVN3PZ4foa3FZ2AUKcIw4vxkzHtP5oxji5I+whtT9RuK1Y44y6jK" +
            "Ejv0zMcR632OHiJK3TA/C9A3ktB37BvHbqeJPAeeQCqfftAIKYlHEa0F3f01GVjXtajr2gMXDkO/" +
            "qOv6yf6wf7RZr8fr9UbVTSmejmJq2yCUpek02hgeTyRROmJ3t8fNBKETbMuGwA8YRwGX5xGHnUa5" +
            "hn3pOBorItnTSOhWDxy/O+d+3yKkIAk8Do2lqHqOT0coZ6lVRDfsOQzQNZrONHiD4R/83e/xr/7F" +
            "H5O7kPOTOb//bMyPbzsuTgJefLPidJ5xdbPnJDEcjQbi4wWhbgmPUgIpOZQ9YT8wmwdE6ZR6UFip" +
            "yPuYXeH44uWWL17dcHX9wDbP6QaDdQ7jQFvwLIRKEkeCqzcRX7+YM5t5PHs8Z5ZFXF6eMLQ1+/Wa" +
            "bBzStJppXBMIxa5r2Rc1S2+MVhDGGbQD1X7L7PyITeHwpSQvexhahtYxuJ6qqf7DAPM8F845zxib" +
            "dH1/WpbF5X6/O9rutuEhz6UcWnojkZ7P0OZkWUgzGJ6cTDj0MJlLVjvNReThekfXgRwnWAHZyHKR" +
            "ZNx0EjkNCETFUPY8PlWEccIm1/hByONHIyZ7yVf3mpP5OV+/2XC7zRn7HuM44eure/7o/Sk/rhU3" +
            "r7fclC1/EK141cLhYDE2YuR5fHiW8LAvkYHPIhGIIOTvfJQRBAF1J1GBR4hHEHh4/oLGG9EMA8VB" +
            "8Xpb8esX9/z65Q0v7+5xIgUvobc91ljAMAjohaOxkOcd+3zgvtDMdxFlPfDe0yOeHCdEQQDGY+x7" +
            "BCMfPbSQpUz9CGkH2rLBkxJHjGcNy6MpTdNirCEMJIWKMdZhjWU8HhHE/2EdKJqmEdbaQGs9bZvm" +
            "UZ7nF7vdbnLYHZRtSz46E9zudqSjmFEiWGaWvHUEoSCVjuudw49gPPYQfkjoRSjbwxCgraR1jnUu" +
            "GR8FUByYjQR5l6CrnHEc0h40XReQ91uOphOUgMN2xfahQYWSTdEQew14KXbT87J5znGcoAU8ngtW" +
            "ec08klytPVbbitAPWUQVj5anXG0OxLOMly8E7114lA6U7IjjAOjoTcDNquFq43j5UPHTn39LXteM" +
            "silOBtzvKqrOgQxw1uIQKOmQvAXaOGj7HrsfcEBnBLFwZJMEiaSpO4JAIpygbVqmWcRuuyEe17Qo" +
            "hLI8OT2lLCvutyVRkrHaVWgdojzo9UDZKJSS/34ZcTgc6Pvec87+pnxe5PlheTjs47zIpRpa8eTY" +
            "JwgjGq1JUsVslCFpMUawzwec8fj4YkYUxHx+fE7ZdJzME262B1LP8ssbUJGkLBpOpjHZ0RnfblqK" +
            "XY0Y4Hgx48+fv+arlxvOzkf8+fNv2W532HqNlYrtTnM6D4nSGY8/mNEc9nz2vYx1afHjMbt9Tb/V" +
            "9F1PM/gk2ZjFOEGGEVE4pi58wrQnmk2JlCBNWhyCJJWsth3PHxzf3uz5+RffIELFZDGl6geuVluq" +
            "TuOcxBmDtWCcY7ACS0CYZsRRAMZSDYZN0XB9n/PrV2u+ud2i/JBo5OOMT6gkygq6uqLrB3zlEUqB" +
            "biuy6Ygo8EkcLCdjaA1C9BitmR8l9Lpld8j//QDLsvxN+Uy7rj8py/KiyA/T/W7vl1UtFhPH9tAQ" +
            "BIo4CnBOsc5bjhOP3b5mNBGcT0LeuxghhEAcLZiPEuYnc87HRxgr+fCDY7LxFNlZojgkUxlnkwlx" +
            "mhFkM7qugKEhCEY8bGvebApmoeSTZ2d8kFVYryYOAg5NxWo/sExi8q3l7o3mYulzsZxSDQMniznj" +
            "IGK9azk9mlN1Fm0bqmZgPh7TNC2TJEINBuMGigFuCni9Lfnxl1f4QcRomvF6s+VuVzNoh7W/naE6" +
            "5/4iBm0RQUKQZMRJikVS9j2HqubVTc76oUIPHkp5dFpjhWS7qyiLmniU0nYaiaMrG/quxVnD2dGE" +
            "PN9wuozphpp+UIQCcAbd938VoNZa1HX9G+03a5rmIs/zs/1+n1Vl4TVNKaaJRkYhbdsTYDHasikG" +
            "Hp+kzCeSNEo5PUlpxIyjWUjnNOk0orECG45pRMp49ghnAn7nkydsCsW+zFlOI5SvmKYtI2VxWNpB" +
            "8NVX30LbUVcdj5cpYRhyksLZ8RzFiNN5wKNlQKw8Lo81XVcxThOyUJD6ktO5x8lYM03G1M4n9iOU" +
            "H+J5El3ukZ5P3wu0SfnidcWL64Kvn79CBDGjyYyXd2sOlcGiwGicMzj3doYoBEghwIETDo3EBgmo" +
            "gCAO0c5iTU/d9rQtHHKLL32WpymFl4HwECgO24JtWdMNjsEoyl1DUVaYVCIGw9EiIvYVWmiSAKaJ" +
            "zyRL/irA3W6HtdYzxqRd150VZfHObrc7WW82cVcVcqo6hIQvXuf4foBSijRRxJ7hPhf44QShMp4c" +
            "jwlURBImmC4nVoJxmCDCFC9KOBwquqHj4ARZApNpQmMNp4sxKopphY+VAaOxYhRYyrzkfl8wMCAi" +
            "xWI8xkVTRGcJgNEsY5tvWCSC8SRm23uEszn5XjDyJYs4IxTgCY/JZIIfBDhtCALB+lDhJWOKxrAu" +
            "fK6udtSNYXlyzF1+YJeXWAFYjRAeHg7POXDurWFhLRIHDqzRWCEoB4P0PMJwRGMEg9aUZUfelKhg" +
            "RBhGeP2eaerQgDIW3QusL+nsQN23JFlEXQ2cnY2RSE7SiInvyBsLKmbf/DtWmrVWHA4HaYwJtNbT" +
            "pqkv88Phne12M99tt/5QFeKTy4j1XhMnU8IgYOgsKvBRyrFuNCKd0/c9x0dTBmfpgzlSjljO5/Rd" +
            "z2FfIUXK7XrNw90NgW6Yzqek42PCMODsdIExAeNQMtie9XrH5tCxyTucTPnm5Q6BYHCSP/nZc9pm" +
            "x3GaUnpLmrrnbHlMXoYM6ZzSDtR6g+q39LZDJS3zQPPOyYwwmgE+Ah/bd+SN5LYOOVSOXV5y9s67" +
            "OCHYHQqM9b/LNoGUEgFvQwgQYKzBOosnwRpL1xvwIupGk8YJKvSZjsYEkcIJ+7aM+nPGcoxyPk3V" +
            "4CtJW3d4SoE0BEmKADwnUMmUpu15+mjBk5MxTTOwyxt8L/3LAIuiwBjjOeeSvu9PyrJ6d7vdPsp3" +
            "m5GrDt5l2gvdd9ztBrAO21WcHid0bUnTWZIgJvA9Yl9wXSluNg2peHubru+2VNpAoNk/3PHq+oFp" +
            "4DhdxLy6qbh72HE6G9NrydU3a6LlGZvCMVIGMVgWKXz8fsQkVcxGKUk2IqPno6cjlkcJLx8KzqYj" +
            "krGEzhB1OevbktuHPWmUcBZrplHAyXKMF4VY42gIaZjSeWP2paPKW9arA14y4Yff/z32RU5ZNYDC" +
            "OosQb0vm2ekpYZqQxCEqjgmiiOXyFBUlFHVHlbcIKxBeSllsWB7NeOf9j9EqRg8+KlmQ9wYTQRiP" +
            "yOKAzjq6occN0Nc1vgDdK2aLKXlZEccTwumUJDRczCS2b+ny8i8DPBwOwlrra63HTdM8zvPD081m" +
            "c1Rtt0Fic3Fy1nGz12RJSNXWpBHUxjLzBJOJT1213NzvQPd8+WrF5mHHartjl2958eaeodvRtQXK" +
            "Uxx2A3/4e59RDB5F3fLmPkcFsFrdMpuH/MlPXlLvSspW88GZ4vGjGWmUcXISEKaK2DZcTBST4xNW" +
            "VUnxcMXJk3dYHQyWjmeTAtfXyN4ivJCT4xHCSZTvo4ce6QsGY9AyoKw120Zj0jHf3NxwfjLjev2G" +
            "oi0ZjMPpHoTDWIOSgjQO+R//m3/Gf/ff/1eoMGY+W5CkI3w/wCmFERYnLDIK6YxAelDWBW3T0TQa" +
            "mSQwOEYRNGVJ7EMYeKRxys3NinEcsDvUpLMxRWVwxoAH+b6k15Y0GZjNU+bzv6wDRdd10jkXaD3M" +
            "mqZ+vN/vz3fbTbbfbtXTcSceHt5OAhZjH9G3LFO4LwsEPdoo5klIvT9wchnxzfMHQmn45puBz3/w" +
            "GFHD9n5PFI7YVh2mOvDq4YFvvs1RDKzzFacTDxzUvebFr7/l/UcjjhYhH33yHvFmIBsqpAfNbst1" +
            "afj4fEK+bfnznz/wycmIqSy5Xx2QTuBoORt7HEeSqsyRKkOot45J12sKKkIlORxqVocGbMD91Z59" +
            "UfHxJx/w/M2ethqwBoyzOGvxpGSwhrv1iqOzT+nSlGw8o8oLdvUGZy3K84j8AOlJjOfTE9A0DS/y" +
            "l0yVxyqX6KYiiz36UhHGHuBh9nuss0zTiKOTczoDpu8ZdIigJgCsSmhtRDZSLLXmQfi/zcDvmpfv" +
            "hrZ6Xtf12eFwWBz2+zCTlUyCgYey4Gjq4UURFzPLNLPoQRMAl2OFAxrn8XLVst+WtK0hjiXV4Z6T" +
            "8YyZ73Ex8QhswfFEsa0MN+ucUWCx1rDe5oxUydXVKz48ifjd9+a0dcv96zsScyAZG168Kag6wUcn" +
            "EhMM/PhXz/nqq2vqymNXlGhTc397jzY+Iw/aqkDrjq6qoB8IAoFTgq5rEMGMoQfTS9bVwKv7Wz77" +
            "/T/AH42hK5DOQ0qFxeFJiRCC+dERzjn+p3/+P/O//vP/BdOW+EjQ+m0DYw1D31EUOW2+wnOGunT4" +
            "CJyAqmw4bB7w44x4nLCYplSHA+Mk5HiecTQ5Zr3Z0uwbvMECHVVdozuLEAYpHFkQMpaayP1bf2K6" +
            "rhPWWs9aG/V9P6/r+rgoipGpS2X7htvS8fqh47NLxXtHkrD3WO0UqSfIIg+HYFcOnJ3OuL+/I5Ad" +
            "ZzMfO/R88XJgmjS8fzbm6+s7Xlxrnj3y+fr5a57MA0YTwWM3om1bfvZ1T10VxLFP5Rz73lLm97z3" +
            "4SVfvDrw4s0D/+TTCYvJiC+uD2wPhrOZT28O2MFhq47HGTSHNUeZQXcjhIw5HCqiUchmV4MMCIIR" +
            "99sNXQ1OeDgZsD50/Og/+YQXb9YMyhLJt5dS/Ft6b/XwgHOW+uWAxdEOAzgLFrBgnKBHo4RDC4mT" +
            "HoiAshkIhY+VDk95WAY0AicDZtOU1a7CDJpdUSDoOFsmVG1JYQeevvs7VMWB1cNrjhYZq/s1vjAc" +
            "jb3fZmDbtgLwrLXRoIdp07bTqqoiz9RyEjvxs5c1kR+im4bY60hGAShHqCx11ZJFks4MbJuWd88i" +
            "+l7guY6yryibmkls2NYdL9409H2D7BwfnmQsRj6mCpDG4VvDs9OIz5+e8c6TJZ7yse3Ao5OAn//y" +
            "lq7YcZ7BNI4xsuZm31G2NZ8/9jlL3/5Muz8YIt+hVM84hKprSNKQNA3ZF5bYl3TdwM3dimJX8+X1" +
            "A/HJkuV7T3lydky/veXf/Kv/k3k25+/9p/+UwPNwvBXuUkqstTghgQEVCDwl8TyJUgInHc5zeJ7A" +
            "kwKlwFMOKcHat7rREwKLA6kI/YjWdFRGMDiH7gWRMiQe7PKKsutRQ89h9XYAHXuaaSI5XcxJY4+R" +
            "3/wW4Hc3TFhrfaN1ovs+1n2n0AN124ODxLMIafnyxvBmPTBOQvbtgPTh0Azo3mB3t0TxwDQ1SDLG" +
            "wYjPnk55KCRfX9fMspDLsznnJ3OMB+tSUxvLZr/n0ZFgXxv2vWDsB7y+LonClhe3km01MJnGeEpQ" +
            "1g3fvKlwTcdnJz3TWYSKNVVnib2G66LHc4pR2DLyDZE3YKWk7z22ZUMUeHjKQzmP4ySjbzQ//pM/" +
            "5dPvfYAMEo4mE8py4Pt/9LuoJEJIgRACYwwCwAlCTxL5CoFDfifmPeUIvwvfl/i+RxBKWtuhnMAZ" +
            "gwosfuijVAS6w9MN9B2TJCRJY0aTlLb3GNoWuhrTw83dDTEHAj9ld+hwvsD4EUJEvwXY9z3OOeFw" +
            "nrXWd9YoiRFZ4MSh1zRVw5OJYzbPEFYwSRPWecP9rufQWF4+DFTdwFEa0ldvBW9TtYSJoawteTUw" +
            "zlLKQWB0i/ZqNrdXzELN5fGUxUjy5Czg9mGPLg94uqGscsZhyrf3FSMfQhVQ1zWbXY2QA6dTDzWK" +
            "qLqKsuoZS8dyYnln6WFNhyc15wtJN1iaXhFmEYuTEzbblk1p2fWWk/MjDq2lu98ySlM86fPoySU/" +
            "/flP+B/+2/8a2xf4FoR1eAgkAuEsTaPJdxWyN8jBIo17O9FAIlE4ITHaEAUJh8oxYAkCRap8Rgmg" +
            "HSoeI5ViOgmx1uH0wGFX0nYF1g3cbPZc3ayxXcsoTdgULYe8YzAJm9IiPP2XZYSUUuLwrHVqMNaz" +
            "xorBaTzlkaoAG0p2Zc9HjyPK3hBLzXIeY2XA6lCzSDRPjh2DjjjPHFpJDrWmHTSXJ0c8mRr6XhEF" +
            "ElO3/KN/+Cmfv/+M8juX/XZnaJ3iUHfsmw7labQbqNqWGsnzb294f+ExjnoS5ZGklrzoCZzk0TwE" +
            "ryISglB6PDlT1DrG80KKQdNqQ9UINpsKTykWscd7jx4xPT2lWN0QWo/8m5c8Sno+fPKMf/yP/ykT" +
            "GTO0Gif0X3KrHG/fQ5z7C0H/V8JCEAT0/YCwgjTw8D3FfJ4R+RGeaLB9TdMJ8kZT9hrlGaaB5niW" +
            "kgVgBkhHYwJWPNFfAAAgAElEQVQl0G2OtDUCwd2bay4XikfL47cA67oWnuf9pokJjTHxMAxB3w/e" +
            "0FrEoHl6LplnIcvEozIWrXs2bUJvDFHgiPyQf/BeyPM7gelqau1ztx6IbMvf+WSGDCUvVoKz2PHp" +
            "eycsJ485nn6fyWLMr15c8fQsI68H0sjQmY7bh4qjBAarWU48ZqFhPJ2QpZIoEqi+x7cW01qyzLDd" +
            "5cwCxSwLccLQuITNTrGqWm63FWEo2exyDvuaNEmZn5yxfOd96t2eIIjYVx1vVhtkMOejH/5dXv74" +
            "X/Pxe4/AGeR3Av5tO8NbC825vwD5m7D2rbXmrMUTEoFH1XTEPsRBgBCOk8cZnemRgaNtayIhUdYy" +
            "yhSTaUoyTghDgYfHNPPxZYeQHUpapNVI0RP7YKqew6F824W2bYvWWjrnAmNM1vf9RHddTN9KwSDG" +
            "/oAKPAbt+PR8zNdbQzKecPWmJAsD9lXPJ0vHszOPTqaUrUGbhuPU8vj4hMFLoD+A5/M733tCmHoc" +
            "tg1XL59zfjrjg8sxu2LP3bYmoyVQgtPJwDSGUeW4Ljwkgp9+XXD+kcYMMArA9Aes9alqH3TH6dGE" +
            "XdXgu5iHTYWiIq8Vp7OMbdkwSSZ4wYjF+TsMfswXP/8Jo0wxW85pxdf82ddXtOX/xuJf/F8sPI9O" +
            "ehyPJ+ihYt28bWQ68x1MKXEOxHcNzm8yEimQAjzPUXU9mVIcJQFSwWQsOJrHhIGPMY7JfM5DnTOZ" +
            "TomdQMoOrXsSpegSQTN0JGnAIBRVpxnFiiLPkVISLOd0zXfbSYfD4TsBr0dd153UdbUsqypxuvHq" +
            "rmcRKOquprWGddmxayW3z+9xQuOHHmokOJk5Xu1D3jw0PFkqsI7PLpfMzp6gQ8Ozj4/4PBnz5a9f" +
            "8VQlXD/sWR+29NUt48Dj1b1GmpyL0xG7+5zZNCQvQCiPri65bxuWSUsS+HQq5Ks3BVGskMLj4aFi" +
            "kkjWtcYTIetyi9MOT1hmaYhUMfNpgO8n9C5k6Co2qx2/evmK49SjLGouFx6+irmuatZdj1IBZpKQ" +
            "JRH/xX/5z/jT//v/5Y+/ekFRDjS6w2iL9+9u5jmLsxKNh9aOWeSYJhFREHKxUHz+/invPTonEAbM" +
            "wOA0XpxRrVZUbUM2GRFGKV2vEUqSjqaU3cCge9q2o6slMh5IoykPqx6EQdV1LYZhkM65aBiGZVmV" +
            "79R5fn7Y79M320KGWotaDBDEZK7l9VawrlqenmdI0TCLJVJ1XBeKXz+0SCM5mfsYYC0E44nC2+fY" +
            "MOT2ZkcaWa7u1kQUpKM59+sNT84nzCaC4+iIF3cVi8Bnte0IhM8m72ibjpNFSid7BhHRFB3LqWRf" +
            "SegbWjmgGNHe9Sxnlq4NeChKzsce4yzlrvXJsgV6GIhjj77vmGcJn188pupyfCGJg5DRKGLXOqrc" +
            "8kf/+R/yi+fXlLs933z9JYPRLEYRQhtk49NZjZVgjOE3O5ZOSgIpiaUgDHyy2Cf0HMsJfPzOkg+e" +
            "niNtg2cd0llM31Du10ymMalQlKUmLyyD8BDSp9US4SoC5UinAb4M2VWGqnB4XoESErXZbIS11h+G" +
            "Ydw0zWV+ODy7X69O8906Vl0hQ1czmQb8+puCp6chuh1IpONkbNlUlvvCkAWSygv4/APD65uYZuhZ" +
            "TBRCKaoWvKrj0VLQVWt+9nrHJPD4/OmY/+frDR9MHWVTUJWO09THdoYwUuysom8ti2mMEzHHM8Nu" +
            "O+LVm5ZPnjjerBvyBgJr2LUOk1oWuqCQkk2liL2YyXzMvnKkymdoKrIsZpfvUCoiDeHyScpuI1HB" +
            "EQ5F1Q083D4wPAL55o6PE8Xyk6fEIid7tOBy4TM6ecRP/uxX5E1J02nqqmZA0rcW31NI4UA4fGE5" +
            "GikuTzPeOZvz7sWCcaoIJDjdk+drjB2QbUklA+JYMZkcYW3PUB2ohwGVGMo6xpMO7QT7RmMR6M7h" +
            "+47lLEA1TSONMaHWelHX1TuH/e7JfrOaunqvAlExO/J5fjuwHIc0veEu10T0lKXHr980jJOYEsNs" +
            "qtnvYXwckBhBJBSrNuTTi2cYP8V2LcLNeZJ2OJXT64j9Jkc8moNzJF4DRrOcaSo7Qu8bHp9M+Pr1" +
            "hqOjCevVmlfbko+mHmUpqFrB83vDNBCcHMN+X/PoMsSXHl1VMV4es9p3QEDbloTJmO0hB+eI4hRj" +
            "WvreMZllhGlEV9WMEp9JtqSvK7I4Q4SSqpngBYq66zA2wyqJ+PgR+7xkaHo2u4JOG+qmxSAIPQiV" +
            "I81iTqYj3j1dcHSckSSCURowdBVWNyRyYJ/vaQbJsK+wImE8s0ht8f0A3w6s84auM4zHKYnnYBgg" +
            "jIjmkjCI8T0P5Zz7jX12VFXVRXHYHdf5NjoKS9mIQQzOo2lKAqsx2icvOsKxQEpDIFPmo54AyXxs" +
            "uJge0XYSTccoC9gcDL/48a/5YFJR+yk/+fI1n5wPpCkcWo1TlnzbMp5D6wQ//3bge+eSVV3yaBGz" +
            "GTo+eRzx9e2Oqa94MlacTX02teZh5/NkYrk8ctxVsJwFxEnCt7c9nUkIpKHVPodeg7CkmcWaBs8K" +
            "nDbEY8UwQBS+/dsSBgLlCxIRkUcBR7MYbRpmJxNcMqHL97R9j8DnfHlE1Wl22y1N2dNVDUVbIT2F" +
            "5wmiyGc2SRnF6q2wly2x0/i9xvQ1Rg8Uuz1N1XC30yRJwFhm9AMof0p4NKG++QUTH7ogJAwtWM1k" +
            "EtM0Bt1Y+qrAOI36rYE9zOu6Pq7KMtNNoeq2FVnisd70SDuwKS2mc3z0OMT5HodiIMt8jhP/rfsv" +
            "Yq7XDX0Y8mQacXkyJVpMePnqNW0c4gcRu/0ecaxQRDgrWASCSeYoCsM0jVmXPa9vezoVYXz4/iPJ" +
            "zTCiHioC2fLZh0f88vkGi2SaGN5bSla5ZhkAUcSbreVqU3E+ifnpN5bQr4jShFmaE/SCTkCnJeFQ" +
            "Yfu3u4BN0eElAaFSOAQSj8ezCCccuAFTPuC7ijhNyUWA0RZfNUTxiMXiXQataasarWu8IMA0JVJr" +
            "fN9ihgFfdowigel7qrqgLRvqPkAFI6pDzzhzeEB7yGmqFotEuCtgwOHR9ZautzgHeIa+77ASPGLC" +
            "dIJyzilrbdr3/aJumvm+KCKnay+vOzH0NetNzTz12W9LPl0I/v6Hp/zvP9vzZBqyetWQjxSJF5NK" +
            "wU3dM7YR2WXGq03P0UhyNj9GeppYaI4SwenJjC/e7CnLjnfOejbFmMANTMOCMm959mTG640hMQV+" +
            "eMTP//yW3/1oxiKCb24LLpZjXlwfeHo24vkq5zRTxMmYUAR8/eWOzJMI4ZilDdLz8L2BLBljfVjd" +
            "VyAgUj5t7qgNJCOJaxTBfEZd1kTTKS7UeI2mdyCpkbUhTARx34Ev6XqHR8dgAKfxjCMQQL0FJI6W" +
            "sDMIT6M8H10ajN5Rdy1tbulMTodDJhmBFxMIh09PGHq4IMT3JG1VIIVP2fb00tL1DlHVoCD2Q9II" +
            "nNeivpMPk65rj5u6mvZVFQxVK4xzlI2lbi1R2BAnCuMLfv2mp20NvYVWSOLvlFAySphYxSSNOL/4" +
            "iO3hll9d3/IHP3zGm6+vOF2OQCryfc3DumHmdaQqBtfhdRqhDe+ME56cT/hmfc+zSx9nFP/Z779L" +
            "kA64tiYOJMNgef8i4yEfeH9ukdGCeDLmq6sdy4ViaDqiULCchFS5ozA1TRWTeJbvv7ugaBXKAyEk" +
            "CEfTDfiBwxUNylMoo8F5lFWOjBI6E7Dfl6TlawgWSJcQuR7T9cgwxY992sGiy5ahz9EkxKrGaUXd" +
            "9BjRIYQjP1i2xcBinAE1DILIk/R6wHiK0SjFWvt2caYdqCrDMLQ0TU/VabSQnB9N6IbvLD0/wDn7" +
            "/7H0JruWbVma1jfLVe/qFGbHzG7p93qEh0coIKVQZgQkjRRCkA2EUHYgH4DsIFp0aKAQErwbKJQS" +
            "CkXhuPv1W9i16px9drXKWdLY/gZLmnvsOcc//vH91wP03m/GcXrRXy6rZepNCE4M44jMim2ncd5z" +
            "mhI/ZM3beaDv4eVGUVaKOXhuSgiXhVWz4quXL/i7f/yZX34uuTGG7//5eyzPfP9+5Bc3itk7vFj4" +
            "+h68DPRD4nVlEaKkLC48n3p0cKy6FcfDhXpX8dPHRH/yrKqWw+K5MfCidlTtLeNS8OO7gTw5XqwU" +
            "Q9FRWclpgGgNa1mxxEwgcDx/xNo1IV3HMEIaUo5MTjD3e5TSCFcQTpqAYN1oZGFJpSCLET9FbJ5I" +
            "RaIsM0uaOZ3OEBOno8BNC8o4nFUYBmIMxKiQleVmt6a0gil4QsoEJfAuUVWJsipRhSBki/ERtKG8" +
            "2fDxcCSHhZtViVIVLsPh4nl9t2ZxCrf06JRSGUJYz/O06/uhiW5Sh35EK8vxuOBc5DhlvrzRrDrL" +
            "/pS4XUdCzKxrEEkhteXiM4wRZVsU33H6pMm5IUwHXMws555vH0r+449ntiohzAqZFxrp8FlQolnp" +
            "QF6OfLnNiKyZneTy7ki7WnNJjk4rtl/ds99PzDnw/3x3YJlmahF42EqsjahyRYoLbaEJuebj/sjs" +
            "ev7wMbJpW+ryyE1TsFp1CBGoagkxcbPrWLym2txwPiyE5R0//PDEZq0IaUXZtqQ58uMPbwl5xXZd" +
            "YAqQIiKRfPZqjQ8FMi8ED8MUWZbA0PfkUXPimbgMpKhY7TbgM11bU9cr5nDmvJ9p24JRCNTuhund" +
            "R0pRYsrMOF1wCIZZ8tmNobYjx3NGSIn+o32+dc41Ks3GCidE9ALluYSZSx+53xRMMdKkglrs+fyu" +
            "wwXJblvx9vuJ4jxx/+qGvnf83T/8ln/9qw4VIzIHznPBP39/oCwEKkbaZPn8jWKcEiLP/MnrNd99" +
            "iozjwC9fb/jD+xOdtRR1DemRqn5JP+z5619/zm9+Gjj9vOfn555xXPjVqx1lp1DRUeuSbDR//8OF" +
            "p/cjtsxcfE9tCu6qlpXNWKMxVXG19CmD1JLkI8toeYwnulLw/od3KNZMrmdaIqde8nw+U1dbWj2z" +
            "bQK7e0OMCSklMQp0VXE+PqGkJEqJSQvL7DEqUJnA/rTg8VRVAWi+e3/AKoWQJYPXTFnS1S/4/v1P" +
            "7FaKfRxgDGwLiAJyNOScKbuEEIHTZIhakdO1jTAxhto7X+nkNcmLJUXSODEsisFHsgyIJJiWGa0a" +
            "xgHKxjAOmZubhi9fgQzw+Zc7fvfoeP4U+Oyh4OdPR754c8fT8Zkwn1HC8OZ2oiwNMXm0KnGiwGpD" +
            "Kfd0XU18f+L1q5YwL7x+85qn48CXX77mNz8d+HQ4cBwd3mde3xfsdjuczDwfR96eFTe14NvPP6cw" +
            "mhAd/nlCak2qLP15pLKJTq1oqoIQIi5EXtysuOSFmATvD5kpZKI7IpLG+5E5Gt7sSqQMtKWhtJrz" +
            "0wlHpC0KXIJ4PhJCRKaIN2uUGFhvKubJEbzkbuN5PCVEXDiNE6e5JLjAc7+nKHrqXcMP7w5UYsEl" +
            "xYO6EFvLp/2ewhpCqEnRYcqCulBUqaBoK2Yf0SmlIobYeO/K0+jU02kRmStZYV3Ci6rA5MjdbYmb" +
            "BCFloimBQKkK7tqCL3eGaBswitvThedx5IVc8/HZ8Z/+YqYyV7PSqRdUXUfKCze3K57PM/0Mhehp" +
            "OsPb/cjQgzcWkmbe79ndPfD88YwQAhcyjZTsNpamLVCVxZ+fedi2HC8zy/mJ5+NbEjXzkHi56vDA" +
            "/jigpWW4eH5/OXKcSjZNyfEykOOJUyp4fhx5uFnhwsjXn33N4f2eUdTcl5HjrNg0ifLmBiVW9B//" +
            "b3RZ8dN+4GbbgUlMs+PwPLG+CZwvPf/4TzNvbms8ivOcSFpz296QpgOFjkileTx5/NnTuoxYZoqm" +
            "4Pm8ME7vqMuKul0zTzNaTLRNhhCYBsv9i5Z60xEvIzrG2HjvV84tVT8tavKeFATTLCFFkAm0JGbD" +
            "y9c1p+PAy/s1OQa+vZWMyYJvqCrJ26eBrrHcNZE8DxAdH/Y9KSQ60bNdC378MGK0ontVs25KCnXm" +
            "jCDblqd3H/n68xuYPZeQEERSeM/pMvM4wJ9//QY/PjG4SHezJagKU1TYukLNM0WxQhlNiAtzH/l0" +
            "VAw5EoJCSE9nIi/uLUNwfPzhBGbDj/uRjX7H67stPz++py5rfv7uO86L5mGz5v3xzG4ncWFm/Pln" +
            "kvyAKWEaL0wLvPs487A1rKxiMJEf//BIvakwtuT/++DJYeTVfctxMDzlQChvcHFBy0y7LeinHqMb" +
            "AokxGWxZ/xHxVSFdYGssstJIFZDSUJaS2gqW8xExOXSMYe2cW0/TXA/TrIbJC20FWylRtoaYWVcl" +
            "61WNoueXn29QZGxRQVEQn88cksWkzMYo1k3LMFzwyrOuBceDo9SeVhpS8Hx2b3n75HEu4byjWhnq" +
            "MvOhH/gXvyo5DtfG/ePTwlcPFUuuWZYL37ze4tMZWa/ZrQVaFThpOQyZ0+kj/ZLAQVsKBieQZs3l" +
            "csbYxGZbMjrFvpf88A97Mpp1Y/niJnLbrnn/88DvHz1/9tmO8zwxISmsQlnPpmkpVxv2774jaME8" +
            "epQRtHWLGM+M3vPhKVJ1NU+HRNlanNMs84TRLVjHx0tiUyc+PQ0MYsHFjEiR3cZStjX90DP7SJIB" +
            "ckRZCe6MTyBMQSMMRE9dVmRpiCRqo1jf7tDO+dtlmTdxWQobnSxy5GUrybK+jmsUJAeGRGsbXu0s" +
            "VkikqHg89Hzz9S1vP3zgfJa83nT4mBlVjTz1NCYzj89Y60ELEBZlBC5AUWaUseyfYVU5OguDX7E/" +
            "Dixh4GazoQ817unM3d0OKQVpmVFWkU3JZZnpn59YxoVsC9oCehd4fxAkUTPPM0IXfDqeeTpnpBS8" +
            "XGvuNy3ZKT4cz7zdw9I7mqbmvmoZU+Dr1y9RMfL7tx/5ee95uGv4w2+/J8eRn32i0JFqqSit4m57" +
            "x3fvP/Hk4b6VfP1Vw7u3E20TqesVZy8ZTwui0izNCuVGhA8UItHqTK0ysxuoReb1m1dIlRguR0K8" +
            "TurXtcaPF54XQ9tuKcRE2UgiJfbmjt5r9OVy/ra/nF/6uS9DmOVn95ra1lyGgbrSmCxYrSperXeM" +
            "w4GuNIQpoXB89nrF8XhGiRWbLuIBkgAZKUxgSo55SXS15NMxopRkXSrKqkSbCp8lc4rkRaJk4nhW" +
            "WO0pqh2TaJnOI693DTFrKhWItKScMSLSVhVLtaLgxGUMDDEQgyfOPaFs+TjAOETasmF2nttNgbaZ" +
            "0Qty8Ly+WdOsGobLiJBQiIW7tuX4fMRQ8Os/+yWP+3e4+QIpo5SkUwk3KyYC9abGpkx3c89vfnzm" +
            "8YeZdt2xahrePwWaLrJeb5Exsv3mW54+BZJ4h0+Z3W7DX352x/vnBf9h5uFecxrfEqRlWyusKhgn" +
            "BSkjVGIZPSId6FXEfzrzxWefM47vqWqN+q/+y3/zvx4+vfv64/u3WzHszar0UiBp64pWw8OmwQrF" +
            "qjMgIrZSlBa6sqBqahYfKHXmYddRlJbx7EjjMzlMFIXhPMyURqKsJsVr77JMirLsOPWO5/OCMjUx" +
            "GwICXXQ43fHjh5kvXzfIuiJ5jyorlC25zIIkM4UypJxpa0UYBrxtEFXNySnm0fHtQ8nLW8uXn9/y" +
            "sNV4EsoKNt2KSz9drY5lwatXNzQVGCtAKagqjstAfxzwuiIvZ+53it5nGlvx7vEZWbZ8ej5zHkdM" +
            "qZh7h7Alu41hV8AXr1ccQ4FfZjZ3G573T6wqxbrVPGwKuqrBz4IPjz+SpSBgcMFhtKQfF7S84jkT" +
            "CR8yziW830O22Cqh1Am3OFzWqH/9V3/5f4bzx5tw+lQ2tpdKCtFYxa7TTENg13SouNBUmdvtBhEM" +
            "QiZspSHObJuarlYcns/44Akh0DTgZsWqMRipOI4z682aypYk2fI4ljRtw+F85tNpYFcXLD5gTcOQ" +
            "Oj6cEnfbEltZTsOEBupCIuXVm2mqAhclKXiiV1xy4jc/j/hh5uuHir/81RuEELz/dOLDvkdrSddU" +
            "aDyShV++usXFxPvnMzE4tFIgNGN/4eIiY0ikaSDZio9Hx/NBsp9GhHVka/ntp4VV0fLjPvHD+4Fu" +
            "VbDa7vjp7RO//9jzdt/zfPHsnwNF0XI6Hhl9ZBkjddmiCkG7KjDCo5RgvV6RBUQfqK3m5d0GUZZM" +
            "54GQ4nVncr2mrTeI7it+/mSoW0nVNKi//osv/69leCrFclLn6SyePu3xwfOiLRFaczyeeLFryBkK" +
            "k5nOPffrhq4ukEiMljg3EYJHxEBMAfJCGAMqX6iKgsfHM6YsKOuWnw+Kp0lxu20Ry8BlctQ3L7G2" +
            "Q4qKn56PnIeZl5sd8xRZV4aurlkWR0oB7xPee4SS/PDxwj/8eMLOE9++WbHadAxT5vk4oGLi/qYG" +
            "Elk3rJoOGWemkPFTIFKg2hUpOTKaw2VmiZHT4Hj/HHh/ykyjJy7w7Z/9Sz4+JYbzgJUljbRkN9HW" +
            "ni8+e0G33rHTAmUX7lav+eqbP+GyP1G3mewFIcCuaShKxfF85PnxmeguZBcpNRgu1NrRNhXjnJgn" +
            "wbv3E9NwYcqSIBQ+R0IOZD9QlnB7c4MxGvU3v7z7P2Q4SCG8eDr2hGXm3I8YobjbVPTDQGUTViqs" +
            "lKwqjVECET3BO1Jc0ERUjig5UVvB6XgGIutNyTROrEtL1halSuYlQLGhKEvc/EwKgcMZjHL45cDh" +
            "dGTbbbHG0TaC5APHk2N0mWGB3iWeB8k//u4J6R2/vjds7loUEp0cNi90dcWp9zwdzugc8ckQ/Amh" +
            "FMfF8u648HSemfsLfkn0HmJW7Dr45k3FX/7pa7682xHyTKEV0zxz83DDx09PvLrRlGUiS40is1mV" +
            "BL+wvxx4HhNKWRZZoVVBmHsynpc7Q6k8N5sV93cVn7/csmlLXr5oWe1aTLOhaDaMwwhJcLtVtGt4" +
            "8fkDz3Ngf1Gc+8BlCVcbf4TnPjEsEr2tgjgMQnz/7pl5mohuYdUUXJaBeYDScD1A4clLRBUlmUAk" +
            "oYSjaBoWp2BynC4TUmhWpUIXBqkMk1u43Rqm45l2XaA3Le+eD3RhRNkTTe049zNqkUgSb7oaU86o" +
            "rNAxchoGfj5m5lwz9B7ixN/8asNf/fVnXMLCMC6ApGwtL+5/SX868Hx6RpYllVjz7nlkXAr258Dp" +
            "cmHXFXx7r2nqLV1l2F8mPpwCwnieexgTrPYXjMm8urvDJ1iOZ27Wt1y++JrXr1r2H37Dq4ctx+NA" +
            "iAExL9TVChNPVObM+dOessqsXrxgmjxLGolUNDGxLiQrAy5ozr3DEZmGmeUSSNFjCktpa+Yh0p8W" +
            "bra3mPBEzgUBSVdl7laWdl2SpUX/4f1BvHs84JaAVQmtBDnM2FSSnEMuDrsMoAuyKpkuDiEzQkYA" +
            "ypyYpkicToSY0FYikZRGEImUlUUAuyZg04llPvGLm4BIFl1IKqWolGRYNNFoECVKaKYp4hbHUx/5" +
            "j3+4sGoD/8V/8jWvtoqujFBmumyxombqe6b9xG8vv+U0wIf9hfMske6J7Bc+3yT+4s0avfmSH57O" +
            "rNcbcgo89tDHQEwDLAXDAuc5IDbwZt0yu4DLma+/ecPH/SNft4np3VtedA1LmJDiSKEltw8bMCWu" +
            "TTTbNUNf0Y9PYBObleLYt+RgkELy/OwYjGazK7i/7QgukeqK4iFzmC9MfWB2A+OcqYzmcHhit15f" +
            "ZUZh0EhiTCzZXs/h3/2bX+e5f2LTbZjHC5X0rEzE6kRnLJUSvN44hNEIqSlMjQsBXSgImRATbdvh" +
            "lx5TFhRlgxsudI0hy4SWBY+Pj1g5UpUli5+prLjyqJ0ixwk3F8xJcaZlmQNRCvaD4vNXr3DRIbKi" +
            "ahoKq7hbB4yJ5OgIueBy6XncH/np+cxpEny2q9jWsGlXKAmrRtIfjpiyYfKK9x8+ocmU5RrR1jwf" +
            "zjwfRsZxJOoW/IVWQzQlN+uO7bZmXi40bcunj8+0WnKzW7PbbJA2Ecn4y8DpPJBkgShK3FJQ6Yms" +
            "Cs5u5DgGjFS8umnQSvDhseen/YkUMq8fXqB1uspk4540C7ZrS0ia8xB4dx6wUmKkISRDd1vRNB1K" +
            "BLqqRvz3/+pVBsHiYJgnXnaeL7aCVsNta8nJUxf5urudrwq6SxknLckFEGCkpkgzdy87SmMpqvq6" +
            "7CEFeZnJKiDS1SIfw4wXHXEJoCXkhMOwn1a8e5r4eisZ44zd/QlfvHlFcBPZDczzRN8/IfLC0cH+" +
            "snA4eUTO3GwkX93XdOuGZnuLWyZMTmhl6M8nwuKYFkVZNaQwkZNndpHLOCNEZnN3yxIz//hPP6FU" +
            "oDUlP+3h7q5E+omq1NzdrMiFIU+RsrYkEbmMnnGJbFqFVA1Pp564RJ6PMw+3K3yIDJcj2ViqwnC7" +
            "MiglCEmSBNxsO1QcyUnxYf9Mfxwpq4bJT/SjoCoM2lou04xICaUMVVdji46madDGoMmKFDwiJ0oL" +
            "sxPX5tsoTv2JdWvwDrzXCOHROhOocQ6GWTD4iYrI67UlxoTtKoqmQelEWxW4pQUiY3+h1C2X/plT" +
            "XxK8olAKQWAh8/YMv/7mNfG8Z91q6jJQqIlP+w/I+Zm60pQ2cL6M/OG7nv0h8i/+4oEXt1cAQbVZ" +
            "URUNq9UNBEdyE2P/TGkVLsLxeGCeTzy8uodyDccntNLUZcv7D0+IwvCLL+/4ab/w9t2eQ0j8+E8n" +
            "NquSQmV+9/E926bgxa7k/aNnmZbr48Q2rIsbdJ5YbTIPf/Irfvz+CRsnVFmyuIbv3x9RWdFVJUoH" +
            "piWCsoznI51VVIXml9++II6a7CKfLs/0Q2JZHEOI+CixRYdUknVb0243NOs1WmnUm131tyI5rIXC" +
            "KJKPrKxmXQmaUpISaFsRkyIkzegFfSyYneHiI58uCz4mjFEIoUiiIuaILMR1Eh4rDsNIzIaMotps" +
            "sKXmw2GhLlpyulrlX9x0SBVx/RlbSFRamIY9z/szVi2EePVEChlRomJ1u6NqGrpVRddYqqamqSqU" +
            "kldrksCko60AACAASURBVNIUheV0Ghj7zG7XoQRI3VA1N9SFpg+JHz8N/ObTxHc/HemHmftG8nBT" +
            "sGsMXzysaO3ItvB882rFZmX5/HbDblXyzZdbqqrCWsV56LksHmMUz089Yz+yPywcThPrbkWpoLLX" +
            "UZXRileffc6SrutqEstpHBm84eNTjy0Cw+Q4TQtVVVMpjUBzuPTMzhFiZE6CfpyZzyfEf/2X69wq" +
            "8DFgS4sRktsCOjuxqgJWSGyz4TQkYoYlgUejVcv+fOQwBqTp2FrFTT3zeldT1hbbWG7vbiiTwTOx" +
            "hBZtQaQB/Mg4cfWmxMDQT1RVICaBWwSm7EghkUVkiYLDInn3cWa1LmhbQUqJi1Nkn3ixLqnqzG5X" +
            "s+5KhCxBKqy5Ot/Ow8DjceBw6nn3/sTp8ZkhCGwhWRWwqTWvtoZNl9k2Kx77gXGKfHoayMWVmX2Z" +
            "R87DxOQ8VbFi3ax4dSNIWRKlpSlLpn64tl+lZnIRrQsW74loulpS6AJkgQ+JIBN1XWMk6LIiTDMq" +
            "L3x6mjk974kIbNVymZerjGYLisbgpgtWglQVq7blMjvE//A3D7ktBMOSUEogMrRq5kUbaa0ghoA0" +
            "FS4kfBI4LEIbZI58937mnz95urLiRaN5uVl40Snubja0dUfdKrSwlE3Jcy+o2hUpXNDhiNICmSJT" +
            "P+J9pikzCckSKxaXKDqLD5Is4DBlNusX1IW73iEu4XykH0789OnI3//2SBSCrrXsNhWVtbglMw49" +
            "D7cF9ytFoyXN1tAYS4qJkAVJaXyqmPuRMJ7IuWf2mYxlHBdi0AjjebFaoVTEe7gsghAD69qyu9vg" +
            "BdyuKrSWaAnTsjAuibJe8fR4xLsRYwRal5wvDh8VbSdYVQVCWUZhOF0W8nJkU3coFUE1TEviPAwc" +
            "Rs84LxTWsipK1lWFS5CMpq41WimNKUrm8xEhJTkmujaCVCwREobs87UipAYkyxzReeF0cZwOMy9f" +
            "G7quRouIyQYRI94P9CeNUI7gB7qmIc+eeTxhyojJFX7psdV1j2LyGVEVgEUWgh8+XHurzdrwzRcG" +
            "FxbW6x1TlOSxZ60ysPDyYcO7J88XL2u+fbNje9Myh8zUe7Lr8T7SjxNDkohZoaUlRcc0e6SW1IWD" +
            "UmKrO0JsSJc9+/1MFCUpDoyXgXHKBCmoDIRU0qpMP5+YH+H2Zo2LjmMfkCTq5g5iT44LL1694Hw4" +
            "AJ6i1GzvXiGkQuSe8TJRVSW1kNy3hveHyB/eHRFZ8c0Xt3SbxGPfs151PNysUTLhc0bXmkoUHC4z" +
            "x+cBvSwTuakoCo1PkWWeUKJk9gInrvveKSVsUeKdxvuMtp7aGqZ0Qmqu2QZ5whSZos4gI2VVMY/L" +
            "dS5W1BgiIgdSchjbkqLDKgjJg8+gS4Yxc7eruEwzP3+48K/+s1+w2RXkZSJfjkzqwIjifFp4/9Qj" +
            "Wcgo6qYho5mj4NQnVnWDubOksKVQGZkip/7MYX/heHaUdYOtAnVdgbh+fwwRlUrq3ZoXD5K3v/+e" +
            "3G65TTu+//mRIWRCqakqcMDkO3ZVIgtHCgWvXr9knic+vH8iLhNrOqo2sNuseL4cOZwGmk2BQWO1" +
            "ZHX7gnPvmcYJPz2D1jzcrDlfPL/77nuyEuyPnmne8/Byy5u7lsZIHp9O1M2Gsi3QJNS//NXd3xZF" +
            "QQoebaBrSiSSQkaWBChD8FyrTyoSoKREC/h0TixT4v6u4WFXUKlE2xa0VYUPDqMVdVNRti0iK/rT" +
            "EVNICJGQBcpYSJFp6gl2Rbd9hS0S3i90jeHT88S2M+SYQRlcTEzDwmEAF0rWuw1ZCmLKDM7y037h" +
            "cYqMLjPHyGV0UFaosqFqr6CD0kiausATiMKgJBhrKauOZXZkF9BWs7vtCC6idGa3aVm3BoVkcjNL" +
            "FGy6AqUyq65k17WchoEgLLeblrIQfxxVTXgnUIVmngxpdlhbMi9cM5mGmfly5vHoeTzNVEXJTWPp" +
            "2hppStbrim1X0VSK5/PC8Rz48usHdusW5SJ116KHfmFxjkIKlIzECHOMNDYwe0FCYpVgiQKlBEiF" +
            "MJKmhptNYJ4VIUZEMtjKYKwmxET1Ryp8TJn+NCJSpjCCpikYjz2m2XJBo2JEqIrn88J+OPPqhQJT" +
            "stlqqhT4zW/f8dU3rwhuIZK5vb1n9XJFUe6oG0Ocz0ynZ0KacV5yOp8JvkcnQVNZCmmYY8n+8cI8" +
            "TIxzZHSPECM+9mzaEq0W1puZly9brN0xnS9UaeHrL14yp4lhSFem52pBSMtx9KQcuGsq8hz4tI9U" +
            "TQFyYcISU8v5aWK9qjAqcvGal59/RqkXjqczqqwpi0R4OlHJmg5J4RdO/cARw3rVUtqEKSS12VBo" +
            "0EZzdoKnx2dCLClrw6q0aFuXuGWi1IrHg0OmTGszezQpe0J2TMLiwkKhJF3dQkqgFFpXVLUguoCI" +
            "6WqVi4msJMGFq9PYZyBT2IJsKo5jQsmaeXasdx31dsdlf+CmnNncGLRsGeaeYZqp64btVvCP//QW" +
            "ows+e7Nm9mBqiSw9w3jGTwtunsjZUZUrys8eWIYedzkidEYy0TQ1L3ZviG6gtCXzNJEFLPOC8zMB" +
            "hZ8833934DQtdJWlLgPBe5RJjIMje08OjnnOFM2KUgc+XEZuNw2FHDkfjnSbFc3OkG2iKDXOD/hg" +
            "EG7hefwd++PI7d2KnCeOHzxVAUVXsTUW7xdWKRCd5HCZCJXhpiqJ0eGV4tIv+BhZlwnnFh4PE+OY" +
            "UP/tf/6nf/u0H5E6cpk0IgmUAG0tUkIICakKUBJyRkrH7arg8eK5pIbNqqaRmVWZaCqJSAkk+Oiv" +
            "d2pwKKHIMZFz5DJLZNUxJQ0pssSElYLkAlqcQWbmBaYpsHiBkIb1zQ1BaKBg1a2RWqGVZP/xE3/3" +
            "//6e3/zhkeOSOXsYBsfldCDFQIwwzgvzZSFgsXWHEIpxHEkkqrKksJq2W9OtGna3BduuQCeHLhTt" +
            "ZsM8OsYT7McZLSocjpgWChkpTeAyToSksLbmD++ficlws7mucscQUEZjmg5dKDabFW/f7pmGgaY2" +
            "dKs7fnyamJOiqCzt5hatNLe7NdoWpBCx1jAvnr73LFMmJsnsM0VpSTmiGx1YrxsOx55NV17vo8wV" +
            "gOoFhS2RKSKEImaBF/DPH0befnS8eWEpbUHZScbg4BwoZWS1Kbm5vaPve7RUyCzRSkJIdDaiTOTt" +
            "x5nm1T06e07nZySZyt4wxEgEfv9hZrez/OLrHc36nur5gJs9p3GkURJTWbKASmW2m46utVgFMsyU" +
            "ZQMys99/5HFwxCRxv/0enzJ1vaapK7SQKCMwtWDV1qxsAUJRFwXV/S3eB8ZZsbl9xWpz5LVrrgkq" +
            "Y83hcOGxzygKtuuraJBT5PX9huAS3/3+J8aguN+tiASW7GgqSxgvfP35LUPwLMHy08c9n7+6YRpm" +
            "9ueIdw4RA1YtlDaiO8uH/USMns5olJEEYen9RAwBQYX4X/7dr3PfR6YxII1ApoVV1/D23Z7gA7qw" +
            "KJkISfDhaaIfFpy75hx9/WpH02lWNhG9Y1UEdA74nKhshdESWxW09Zq6UeQUSNlRFhXC1CA1Qir6" +
            "p3eMAdZNizaSwSd+/iS5e9jw2ecvMWVHDoH90yO6qJlcwI09TV0yDQOVrakqRV03dKuOeRzw4xHn" +
            "zhz6C3VREGPmOCxotebh9UuarsRKw+I8i5+ZZof3gsDM5TjipkDvIkqDD/Fqo08TG5uxhUVEz2W5" +
            "XhExK54ez6zayOZmR1wkmYRzE9ZWhGyY5pnbzS1VpTkeDngfEAmmeUTVJd16Ta0yOWayXjFNz1z9" +
            "uVeujjUFdbMBK4lC411kjhLx139+m796dYdfAv109SsWVnE6jSwxcR4D4+wYx+s91xaW223JZw8b" +
            "7jYbth10bcFwmijlzHZjGc4z0zyxaWqkhsuieXG/pbAQkqDUGWs1/TihhEbphe/3mdvbO5qyQAvD" +
            "mBLDFPjTP/sWqSpiguH8gbe//xlT1xhbIuLCbtsQJZSFpRCaojRIqQl+JMWF5/0zUqarifiy0Ky2" +
            "NF2LNoqmMkitkNZiVUZmSFkiCNcfm/OYCpwXPH74yKGfCUEyuUh/HMhojM60RSaQMbpkdp7xslCY" +
            "yJuH++t3zxPHTyeMKplyZn23oyoTdR5xs8PlApGhMImyKIkxM6crxlnGQNSKmA1EQS40ttsgZcFl" +
            "6BGf35ZZaYEpimvFaU0KjnGIOJ8JMVAoMBK6qmaz0vzq28+43bTIHHm91mxuSlSWxMURyQTn0Wam" +
            "VAU5eja394zDhBYZWVm0utLHhmHi+NyzvVuR5YbuxQMxCrJLuPmCrjt8WFg3DUFAJDMejihhWW86" +
            "puC5nA6s6oK2KUkhksLIqm2J4VpZYXEonXDZcDwORDRS26tsqDQ+eJKfaeoCYwzGaIqyREiFDzNS" +
            "CIL39NNEHBNzlCgdWa06klvog+Px0zNLv3A6epJIaKPQFholuN+ssabhPJ8hLYTJEUViXgIiB5TW" +
            "GFuRskbajJsSTaNJgBAaXaxI0bHdbnFJM8+O8zSglaEoKsSbV5s892e8z0QpUDEhoiAjr/QFkSiM" +
            "Ztsa7tqKr795w6++/YqH+5o0zjx//MC2yRRdh+snwrLw5VevyVoSwjXgMbhAygutrQlSkU3Fx49n" +
            "druS2U/Y0qJNy+3rX+BcJMwXTpeJbrXid7/9kfXKUlqL0RohM1YrdrtbLudHjodnxnHm/n6LkYL+" +
            "fKY1kpwiFzdibUVhLRmDywlhbigbS8qZcVzw3hP8TE4BLSVCKLQxKKMprKQoij+SlzIhQD8uTPN8" +
            "NVcVGakNwfdMp4UcYLvWOCTPzyeWZSEsgvk8UVWC1aqkqMxVqQkCKRWjixwvM4WG8xQo6hYtFabs" +
            "6FYrCpPIMTMskZAEtqrRRY0UEhcj6uXrm78lXasmpXRV7KUEcSXs5QyNEfzyi1v+/Fff8OLlDTdb" +
            "y+22oa0U/fl8TTkbAxDZrGpkTnjnQUSMKlnvblHlijkIgvPM3mOLFqEshTEQBEiB1ophmPDRoZTE" +
            "Ni1SaqbJUVnF7c09ShUoFSlLqI3CJDjtD5z2J7qqxCpJfz7QX/bosmCePW7xDFNkmj1Kaeq2RNmC" +
            "4DNCSETKxBBZphnvIwiJDxEizJNjnhw+Z6ZpxhpD3bQUpULkgmkKZGnBlJiqIJOwxmABbWG3taw7" +
            "DULyw7sTv//xhIuepi2Y54lKKbrKgtGURpNjZJpnTqeR/jIQXOAyjBgjKWsNSnE+exY/UtcN6hff" +
            "/uJ/jyEIwdWmd4UQCbRRWCNRUqKU4rNXN3z1YsVNrXn9cMM//POPbKqMsQaRJachIWOibQrmEIh5" +
            "YffyBlu1BAT1+pa6a2l2Ne1qx7o1NOuOSGa9e8CHACkh1VVI1khWm1sKPCle06kLm6isxc+OMJ9J" +
            "y4JzC2Vd8/T4xDRNdJv6SvWdHKezQ0lzTRVbr6mqkugmAol5jpA1AglC0jYtRVFgrcQUmqquCDEQ" +
            "YyTEQPYLypaoqsNWHbooWELkdBiYxwUtQcYISFJM2NLQ1StEhJQCuoDPX214uGsZJsfb9z2zSyhT" +
            "XVueZSb7q6VlvS5ZdQXtZkXWJboqCTHRn2eWJTFOCzl5irpA/dVf/dX/FkKSMWeRM3+Mkok0BXz9" +
            "5o5XtysO5wuHYWTdSKq6QuQFN3ludy2qtDR1SdtW/PB4pBKSoraU1XVB3ypD8+JzjucFlWB184Ks" +
            "JVLX6LKhrrcY27LaNFRlg9aKqm6ZXU9lFNH3V6Ghqji8f4sSI/M4IrJAS0ldl6AktjI8froQcUit" +
            "iVEyjBdE8jRldaXWKxASpsUjpUJJQyJd7XpCk0KEnBjnhcs4EiIoJFIqkpYkL5gGxzAv9L0jLJ5m" +
            "XdGuW8qyYb1esbu/oyo0pZRIAmGZqKqGLAxxcaAVt9uK23VN38983I8oY/jqzYai8CRx3YZ+Oi1I" +
            "FEIVBJ/QSKQuaLo1ZbuirCq61Rr13/zbf/s/Z4SOOV3zK64APppKsC41f/rlG9CKtx9OeBcQrmdl" +
            "BTddzafT83XgGCUhzDSV5Q8fzkwucf/qnrIoQViE1qQUMEahtEUXLbZsybrGScNp7KnrFW27RRXl" +
            "NbVkHhHBE9OV9hedp1m15OzIMTHPE9YqyraiajuUaFHa059HyqJG6ysa0nuPD55utSZeGTxYbdBl" +
            "iY+Oqiqp6gplSoy9ChbaFBhtScGjhMJWJdI22LqjrFu0isTkUSSEllhjENEzu4VxGgnLyHTaM/ZH" +
            "nPcMo+NyHrFFSVaGlBOkSFMUdGvL83nm73934aenmYxiXXdoeYUAZyEpi4IQAlqDVIJVu+X2/jNM" +
            "U6H+/b//H/87rUyhlNJCSCmEEGSEQLCtEt+8uWOcAufLmcfTTKky/eIwSnMZR6besb1ZMbjIMp15" +
            "/eYN8wjRzWy6jnpzg5KGrmmRpkRoi0DjpoFp6kEYpsuFm9sHspCEFHHp6jOVSiCQ6HbF/tOBTavp" +
            "j5FmXZMpaOua0UeiNPgETbdGCME8jqzXNauu4PbuBh9gOA80q4Lu9gZEQUKgjWSeHUZYEAIpM1nk" +
            "68EvCzJrrC1JUqOUJeR8/fvOCSE1KWVinAnTgPATcbowL+MVRqcFVoOVmcUthHGiUgLnZpz3V01Z" +
            "ZhSKVVlQiAtawqdDIiCpWkthLF3TIpTB1A3tqqbpWrrVmpAVl+cj6j/8T//hT4qikLYojNFGCykV" +
            "COFCEiWZdSVYNYYkMpfZsT85/JKpSsmLbYM2kufDheASISxYrRmSx/tAqTW2ayjrkkvfXxUSAT5l" +
            "fJi59D2malmtbvAhkQtNfz7+8UEVWd89II3Fp4wt1yQ/0207xvkqManimlOEMCitETmgdWS6nEku" +
            "UDU18xK5vdvggydOI93uFucCISVSlBitSNkjJYzDgPPXx1xZ12RhEPKaSTaOI16AlZYYPVoK6qpE" +
            "KoUPnn4acS4Q3UwcT0S/4IInJ4+MghB7XFi4XC6Q5f/f2ZssWZYl15VL9XT3vs7Mm/CIyEQSSIBF" +
            "VJEUNkKyRFiTmvB3kCN8AD4Bf8IROaEIpWpSQ1ZNIBQ2yAxkIiLSPdytec1tTluDcy0AkBSSEU/E" +
            "BmbuYu7v6jt6VPfeqpuSMqWmjqhIwwXP7nTE+IFPj4kPn64Mo+cwBC7ThZIzo9+jGvj4+Mh6ecTv" +
            "A+YXv/iFH4ZwG4ddCSGo994Za6yK0SlmIa9ysJXP356YpoVpLjzcIs1sJK8IChx2gTdv7vD7HWVe" +
            "eHUaibUiCJ8ezuxPgbRGxFjmeUHxSFWwBqd9x+bT45lSGk0tcU3kLNiwx5oB63ZgIRxOXJ6n7S64" +
            "ZwgBwZJTxDtlN96xLgs1Z5wLHA4jt9uMGsv+dMeHb3/NfL4xjDus8VgfKMaRYsKZbvCh6kmxUg3U" +
            "0tgd77DOcTmfSWnFWMVZS62VcdhzOJw43t9jhwHTehG4rgsfHyIfnxIpVcbxyP2bNxyPI84KJRfS" +
            "siC1AYbzLfLwPPHp0xlplc9e3/F8ifzqLx8wxnHYH8i1KxTCbsTtD6i1mF/84her9/5pGIbzMIxx" +
            "GEcJPjhrrW1V7G1KsqyzDF4R43i6TMTUT2M47Nl5ZX93Ym2NmJW1FIz3PC2Fu9MbzOD47uONUQqH" +
            "16+ZbgUxDUVp1rPeImboko1hOLG/+4z5eqOVRBsDS0oYYFpnzt89Ii1Ra+NyvnI87FhjoYiw2x97" +
            "sUFG1XO59Dvn+fye3ThCVWIqtNS4Xs+UUrF+YFki0jI5ly5MNiMgiECl4YxnWSu5ZAbnUCdAr169" +
            "HajEvtFpnZBSCYNDasRi8LajJ95k1nVmXSZKLdzmlZhgWaGJZbotGGmM1vHZ/T0//clr3r45QgE0" +
            "4ER59faO4fU7UoVaCrdbQcuK+aM/+qPknLt675588M9DGOZhGKtzzqsan0qzT7eoOa3inSU2uEwr" +
            "lcrHT2fOl8itZNZbJOaFkg0PD09crleeni+8vhup2ojzilM43L/B373lMnWUo5o9x/vXFCxrSnz6" +
            "9A3z/EzwigsDzlpKrDQNJPH4YeDtFz/n/Hzl1esT4/0brBhA8MNALhFUSEsiLxPWDL3xNpXgDQ1o" +
            "JZPTzHxdWAvM80QYDtRmyDlh1CCivY1IC2KgFBAEcn8vpncfQCan2PtnMoYZqUJzjvG05+7kuhu2" +
            "c6BCTI3bAkssLMtEbYB4mjX4wZGaMqXGZV44Ho4cd44vPzuwpMqH3z6RS+SwGxiDx4Qd5o//+I9r" +
            "rTUZYydr7cV7/xyCn7wPzVg3gAwpV3e9rSpSpdRIjImcDXFtnG8rz+eVORaep4VSlMv5kWEfeHt3" +
            "jzoLIhzv7nl8nmgiPJ8vKI5WM0tcyKnL5dbLxOM3v6UWJTeYnmampyvFOYxalmmiTDMiSqwVLZHj" +
            "3R3JWOIaUSoqsFwvvL57RSoNsJRSKenCMI7Y8bC5igV2e0sqhuPxFQ2obKxLKpRaacEjfg8pIU6x" +
            "PjDsjgyHHWhfvywIwVm8dYgbWHNFVBlGT6n95Na2klMkLpmclFIMU0r4XeDubs9hHzBGWKMgatjt" +
            "lJ0bKLXhDjvC8QSmV/PzWjtSo4ZpWTB/+qd/2mqtLcZYRGSx1l6dc2fn/OKcU1Hd11p3YnBLjOoU" +
            "qZukPqdC0UaMmY8PE2tceL5FYm1UY5hy5cN3F27Xmeog58ZtycT1xuXyzJzhcHfiep2Yrle8D6RS" +
            "iGVFGxh/JAw7nPMsa0SqcP/6LU1hWSdEPcfXX/RVIilj7Qj+gKA0dSQN5LYwHgKlDlyeniDesGHk" +
            "dHqNEcuSIpfrreOjftedyEqjNmilEa9ndvsDNEWNYoPB7/f4MBCCZ5kmar1RUqKuE0qltMLtdiMX" +
            "mKfIbV2xbkSkn7BhGPn83RsOpwPeOoLpi9TDOOK8wYcRF3ao9WAdaxPimnEGgnPEBFmF2lpfufzu" +
            "3bt6Op3at99+W+d5riGEIiIFoJTiailWVX6nZg7XR7VuV3Us/RekmCi5omRqVh6fZr57yMRc+Q/T" +
            "R7747MjpdKAc9jgM1+k9706vGQZH/O6GH/fEaaKUzLTmLt+YExSY10dCcOhVON69JhtlujyyO4zs" +
            "NunG7XlBraDqGY5vWeva5YrnJ9a0sqzdrnt/d0dlJi8rrsHtdmHcBd7e77klJcuItwE/jN8HUKzF" +
            "3r9FjYLpoty0VvLthjpDqYXhdKRFS11v3YslZdKcudwu5FwwJiB4mhSOd0d26vrMIgI1kUplFc9w" +
            "f8I7Ryswx0JMK7EUVJRWEkY86gZUtgPzeOO0HzF/8id/AtCNHPd7rtdrLaVkY0w0xqzG2GqMccZa" +
            "b4x1iNoG2loVURFrBTWtE7Y0aimUlLFBmK8rmguPzzOfPj0gwK9+88B3n56oanh6vLHzhr/4+ht0" +
            "Y0S+/eYD8/XGOAolVoz3qAE7OC63M9Yp/vSKZnYYp9jBgduTayHnxOX8hNHA/nCHGkMtSkwJwWJt" +
            "oFZDLYkwOnKu7Pbdik5dn9+Na9wU1wF1Bmt37A/Dtv7DsN8d8eOADx5nDDUttBKpaaXWTNuyk2gH" +
            "LbwfUAekSCqF821imW+0Woiron7E7feIKst8Y50XRAxiPcfTa1RtB86doUmlNaFWGEPfbvE3XKy9" +
            "9+13f/d361dffZVijOcQwm/u7k6iKsn7cN7tdn9/3O9/Poz719YPo7EfzHR9ElFF6Ij+GBzTtbKu" +
            "mdSE5ynTtPLpeeXxIaMOfvk8k5rl8btn5pz4+psrh+O3fPHlG9ZbIs83cL/HcdyxPjzjnQLC5XLm" +
            "07czn02Z0/0dOc7cpsQXv3PiOs+wRrQkZia8D9zfH5C3O7AO3zIqhdaU66cr485grXSNq1bW2yPY" +
            "e4bxtO3BVqwbcA6MeqwfyGlmWea+uaN206+l9mC5YU+LhlwrzWXGEezm+BJTou5OhBAYU8EHh1FD" +
            "XDNiG3m9sT5P5FpR41hrwxpLbRYXLDU1fDjgnWOauzZJEI53+786gS8vYwz7/Z7b7dZaa8kYM1vr" +
            "LiGE5xDC7MOAtd4jxueKzSVrq0VojVJi35cpjtIUtYaUErF0p+dljaRS8W7g03dPFBqPjxPLLfKr" +
            "33xLWlc+fHri1++f+NXXv+XxcuXu7p41rlyXmWVdCM6TY6PUPhm8XGdqXfBuxIqgYWS5XqFGltYo" +
            "0jjs7miipFppalBV4rKS1ol5mbnOK7ep4MUi2hegm3DAetspJiDXiLSGEaVIhRZZYqKkCu4A6qhG" +
            "cRtPp9Zg/YiYgSra9Tkxd1DCWcLgscExzefNO74xx0Ss4IcBUcVo9ziEirGWNRdE6SuZrSfn9b8O" +
            "4Es6PR6PxBhray2LyLQVNucexICxztfGsCzJzfOsrURprXYDjNKorZFzP/LdaGFzet4a5pwrMa/M" +
            "l1tH/HPh4eMzd/cH5hh5//7MHBNf/+Vv+Mkf/B7vP35knRLDLmCAVoQqQimNWhZiWtCmNAwlLszn" +
            "Z1JMeOt5vl5x40BRIcXM5fnccVUMJvT0tR/22PGO6hwmDIixNGkI0kndumlWgJxWvFpaXYjxxjJN" +
            "zNPM9emJHCPLMiEUjBpqKeS8YFV5/fo1436HiLDGyHSdu7JAHE1A68r9/oC4AFJxzmKNwarg1PRU" +
            "j8H7AAIt5/92AF9O4t3dXYsxtpxzVtXVGHN1zl2c84u1xtXaTtM0H86Xi1vmSZTGBqRSt4u65vZ9" +
            "yQ309cLrSqEbBnvjSCXTcibTfWRbbSzTyjQlrvPK+w+f+PjxkXUp/PLXH3h4vPLZu5FYlWUtTOcr" +
            "47Dn+fEjmM0HqmTi7UyKK60Vnj+8J6fYfX9T4nI5czzuMM7gwoCOxx44FzBuxArY0EVbrVZijqSY" +
            "mKeJXDIpztyuC7lWnBhqLmhT1pIpFUpOpJgppcsqRZRcG7UCDaQJ3hvUCKUVUMfd/Re40PW0YgxG" +
            "Lbk0iijGB5oJLGvsxdMwYN3fdPD8b76+/PLLejqd2jfffNNqrTWEUFtrdl3XN6fT6ee7/f5La8Mu" +
            "NioIlwAAE3dJREFUlm0QpjWMMXipWKMkhNwapYBqr+6Ms0hTWlNyzt3KBtBWuV0jIq2bC6dEFMuH" +
            "3/aK7v3wzPHVka/fPxFC5p/+g3/I//ef/jPMC1OaOIbQSWgUqYljcMzTjImJmiMyCyugMmBC4TZH" +
            "DndH5nUlThPjYDl+dk8tMLhAyjPX26Xjpi8fSGOZbmfyOqNiaGpJdLa8qcH7V1grlLKQYiI36fMi" +
            "oqgJnWGoCR8Gau17sceDx9GIayTViB0HpBkMCjZRrceEAw6YYsSFgPOe3PR/HECA/X7ffvrTn5av" +
            "v/46ishkrT1bYyZVrQ16uiyNNTWCaRgDVhXalsdzoojQqiWb7itEFWoztFaRJkgtvLgT1dYtvVtr" +
            "lJiZUqHWyjxD8555ifzLf/vE46Qcwo5f/uZbbvHGz754y/7ujsF7drbx8eNKCI5xcBzv33YbPZcZ" +
            "djus7sjzE7UasIagFrV9/Gt3OFILtOrJaSLFmayh00wu4EehyYABqja87YM90FDpYDetoVYxAs4N" +
            "qOnE+MsVs8yZIXiss1gxSCu0lmjU7pTmAuL26HJBpGG1b8B49epVx38b2O5s/z/32u12GGPIOTf6" +
            "Q3Y5Z5dSMimnngYFGoZcGkZMH7YUEKu0VoixYcrmiFm6TU1t0j3aU0O1p9+u6OnSjhdjKVpDamM+" +
            "T4SxD+L82Z/9kn/yj/8XfvP1A8uSOE+Zn/2k8jjN/NP/9Q9wx96W/OTdZyzvP+LHgdvtxrvPhftX" +
            "ryjuHTWvhPGOmhPO7SjrwrVWVEyX9ImjWvr3CLmBG0+ITbRSUW2odwzGUtZIa5UQAmkYWZeZmmYw" +
            "IxhorXTwPwRaKoh1GNv96VNriHc4CZRMH0+vCy4MrCmipaLOsT8ESkqoCoMf/ucD+DcsglrT1qot" +
            "pdpSipZS5MWZck0ZLwYU2haJptr9FEKDuqm0tVKKdIxxqxZra4gKRl7uTO2bGKQrxJsKy5JItZJa" +
            "5dP1xq++es9Pv3jHf/zVt9yebzzdImos//qb/5ef/eyeV4c9/+HPf83v/85bWm20UvmLr/6Ssqy8" +
            "/vILiiitgNhArokQdqCelHoqF+MAS2PtTHdtqDeMLvTZj5zBW2o1GGepJSFdEYixA9oMhYYNtm/0" +
            "LWmrJJVKJ5slZ2KNiIDaHWoUsQaRRq6gG4daa0KzIEBcJmj+hwXQGNNaa01EKkgRkaqqTbX3JZuR" +
            "16Zm0xenNnJOnfoQ6f8xbWA3OC4XcurbBKu0773a/8uXSB9zK6WQl0qukIPw1Vdf8y/+z3/Of/zV" +
            "t5ynhHw8M1oltcyHhwf+2T/62+S18dvnG0GuBOmb4j+g+MFhbSAcBkpR9rs7sP2BGWd7KkTxTUgl" +
            "9AYbQ5VO6KoIag1iHU4dDaHkRKuZoEprFa1Qy0qczyylMAx7docDRgppvrJGSGWBtPZAadsylyHm" +
            "zDAMrMtMSjOUQJOF3d1r0AGj7QcFsAFNRBpQjNForY3W2mqNbar6X5zSl68ejVpfTKIEI4oRwdgO" +
            "3lprYAWkIKlXau3lX6QHrvvXbg5hracYTYVzg3/1b/8fgh8wCh8/XvnJlyeeHhP3B3g9OP7wn/yc" +
            "X/3yI8HC8bCnpsS6Zp4+Xnj1Tnh8iLz5/HPmeaFKZTyANwPRNIxajBpagVYbtRac9xjtCFFt9MBK" +
            "5+pqo1epy0KrBWNsFy1VGIY9w+6OUlqfi2zQSkEKiAmIOhChUpGaqS2zLDdKmns7IXnztU9o60Li" +
            "H3QCN3fKqqrRGHvz3s8hhOy8b2q2JQetUKS7elkMKg0fHJRGSYUqdStWtHvTKr151n5yU62klz9H" +
            "qLWX4L0/VVpuUHpyTlvLIsVQ1ojdPG2/+25mjitVG//3v/vP+HdHzPHAh1//hphWduMOHwauU6R+" +
            "OnN6+xnzEtmFHVUqLRVSXrvM0iv43vxXau9xa7+vEUsthZoj9cUIUsD4gTCOrNPEOs/U2tmNeL0w" +
            "TfM2SxFRLZiWuwLbh633FGqrDG7EuEBKC86PqOgm+1C8CX1DRfiBARyGgXmeq4is1trLMITncRyX" +
            "cRyq874ts5FaK6VVWqvUZrGmTzxt3sFUGqXWvvOrdW92Y3prMe4EkUhdSgd7W2+idQtuk95TSu0B" +
            "NJujZmtQayPl2v/cVMbBUXLiPC/8X//m3/G3vviSL94cqDGCG7ss4uBYY+H88IiKZeeP7I533ztW" +
            "K7UjMQ0SgiAYNSzzRBg2sCJlSi5Y2wdcEMjrytoqy3JlnS60VjCmdemhSFdUI6R4peSV4BSRSt0q" +
            "8ZwXVhJGArSKWoOzhmY6bWWtBwzGhh8WwN1u1x4eHqqqrs65p2EYH/b7/TSOu+pc148gHXoKznWA" +
            "uRbaojjbnS11U3wXWh9F2+5MNZ2u8aOltEaKvQBS6ex4r0YrxvYHWmr93hK1W6GyleiwloJIY38a" +
            "uV4X7sc9t/lGqTte3X3Gpw9PaCsc18Dpi7fY4DsJHBMSC/v9Du8DpWYE6VLLVokxIiJ44ygpo2IZ" +
            "h5Gcc1d4x5XSCnmNNGl4P+D9iLRKyyvUggkW9R5nRowKuVVKyTS2NWe1YbSr8FLtuGmpBSc7rAmg" +
            "irUeawJqfmARY63FGFNrrdEacwnBP47jeBvGMVsfvKiKqkJt5Fxo3SF4e/iCmO2elAa1dnld2yZy" +
            "qDRjMNbifaPkTKugpv/1lwAJijaIqfYdm62f6pdrWmmUVtmPI+TCfrzj6XLlJ5+f+PT8zF4Np/FA" +
            "ThOmAtcbxQntcGDJM8wN4xVrBERRA8YFnDUMuZ+QkhK5FuZpZppuqBrUmD56bj3Dbo+K7XdhmshL" +
            "QgHVSo4TMU2YBqZ28VYuGSeGUjZ2XxU77rrSoFasMRjjcd5TRTA+IBpQY39wCm3GmJZzKsbayTn3" +
            "HEKYQgjFWdeMsV2X0mEXVF5SZ0+bIoqYnopEtiptK2jUKKIGFUPomYN1LX3tMD311NZ/p8FQW6Ui" +
            "tO0+bNSusgasscQY2e8HrpcLn731vQKuijOWdb4SvGKcEgaHF4HnK21P13se7zseWRti+gOjrWQi" +
            "aU3kODPnTC5smz26ytuGgPEDACkulE1X6oaRRiPFG3m+kfKClQ4rWmtQlLTEfpVsVq691XDQFO8t" +
            "1g+o9d1h7aXgMeaH94HGGLYWYrXWXr33U/AhO+9RY3s78desuV8uv1Ir0ipmQ/dFBGstm+EltZbe" +
            "4EtHcXww5FwppfbLWxSL9Alio2iF3LpXmGm1/346mO76gAexZnJeCGbP+bryxetXxFo47AYO48gQ" +
            "HNenJz4PAZNWduM7rPfIOlFviu5P0JTaCrrd2zmt1EZ/r7UiAtboBmIIMa6U2KXvrXQHbFQxzqIy" +
            "YKXi1t6ipDQTp4j3jpxX4rIyjF1k3DlAj7Nha2sGvN/1qtd4jHUg7YcH0HvfpmmqqrpuNNN1GMcY" +
            "QqjGGEVFRPWv+sHtpurB7Cy+yDbMYsyWQiut6ff9R6kFaxvOm25xs50trYKhS+rFOcgZI5ZUIsUo" +
            "DTjd70kpMqfGfM2MgyUXx9PHZ37/i8/JuTDsBvKm9nZbU+xe3RGXGy3dCLvPWNeZ4C1NHIqjKqg6" +
            "fBi7umyZaSlTncH4HaKWklZayUhJlJy+f89pnUlTJKeVNF369zFyuj/hvMNZixFPdULY7UF70RKG" +
            "gPMn1AbEBNQ4qCu1FQymt2A/NIAhhGaMqTnnaK09hzA8jeO4hGGs1jlETac6Wvtr7UdDRbc31IHr" +
            "Rp/DUFUwitStUKH/zJjujlZrg5RxohsmKGirOKsb3KVY8UwlIa1wCDseYsO0RDCN/bjj48Mjf/cP" +
            "3iImozpASZAqWWC3H1hvK+Ou4MaMDDsKlqDKcn7CDSPGHNAcySmxrCtiGt4JRSxrjqTpiroRMVuK" +
            "D33/TCmZ0jpJq60SfKatwppT/946jHPUkvtJHjwaHM4N+N0RH04Yt6epIsaCKs6OPSuhiOgPD+Dr" +
            "1695//59VZVorbmG4J+HYVj6CXTQhPbfbSY3kHorudt2Iq21nZnoNTy1KYbGYecpq2ByI4ROsJom" +
            "eDXEVFhzQ4zHXWbEsxVPfSbfBqVVIcZIQfn08ExxE6+PjtPhQIuFcXdgmq7kGLHTjNvtUCo5Nqyp" +
            "+Bop6UxKHZQ+HfbkVsg5k01mzYlWC3m5Ia6nNjHK/njqrVSp1LZiYqFFpTqDHo+0rYoWEUQVZwJm" +
            "CAyHE87v8OGE84dN67ohWwKiHqXQWuljBz8GC3XOtRhbVjWTte7qfVi888Va29QaISndyrBXhvJy" +
            "4mhdrCrSLUVbBekst7FKqVvfJ42UMgq8uu+7RSUWTuOIc0pQZW9GnA3MtZEavP/NB+aYWGsh0KjG" +
            "st8f+O67B1ywXM4Jk4R3P913O7zSGJ3DeouupjftClITZb0Swp5SoaaIOrOJkCJFBTUep32Lvq+O" +
            "sibUjBhjsL5TR8Y5jDpUFdFMXALzc6X6FVFhCGNHpyi44PHOo45tSnjE0BGcJmztWQcKKr3XpUkf" +
            "Rv0xWLaItNZaVdVorZ2ds6t1rqoxvczunfX3J26DtDcWAozR71GL3Pps+hrXzq/VgjV9+tQ5i0jh" +
            "dHfgbhhoOXGwlp14Rn9gt/MsBfz9Z7x6+yVf/fs/pyhkKg+XB1BQp1sflkirUCpYNaR1xYWB5/OF" +
            "wSgGJceIqwWnBuMCqgWViCKY3Yi0jJpuAb4ukDIchjskVJrxlFY79GYsbAWbsd0LZQgDJYzkdWI8" +
            "jkjzrPMZkb6IVmzAOIN1B4w7oUZ7Y98gl4S27ffSUHUdxviRAXxBKqqIJDW6WOuitbaqmlZrV029" +
            "BFAEmkpvtl9+8DeZDUrpFShSGYOjaRcEeWf7ONVpx2f3J8qyMGTFlcJ+r9y9OpIznKcbb46v+LDb" +
            "YUMga+X156/4T1/9ed95g0HEUSWxrivL5YY/7FhjxIqhiVJzRZp21Kgpxjqcg1aUvF4wG4pkUyPH" +
            "iXH3mjDeY81AEaHUTKMg6rsgqeQup6grpfZeFxrWKsYNgEHnijMDajxYg7UDxu0wzncqrWREDd44" +
            "amlY6zv8iKW1fgX9qABaa1uMsYpIMmqWF1Bb1XRBbOugbuvc+F/DS0DV/FVlulWrsvnwNvpKqXmN" +
            "WCf44LCuow+7/cDheMQUJd3O7JwiTQmD5/e/vMOHt7x//551vvH551/yNE+827+mxUZMkVsqOPfy" +
            "IYKUEq02BnXg++l5fnrCHgLLGjD5hAJxnRi8YW8La4yUGgm7E9Y6stqOnZqA14A4odU+ZFk2gS/0" +
            "Znspax+McQ7UsCw3htFhdEBtwA0j3u1w4YT1rhdB8YXVkc7et0YtgrOC2U68/pgASgdVm4iUDmxr" +
            "MsZUVW3GOtT08r9U6fRL24gF3cLYelW64SrbmHNnJmqVbaijEkvh6Tzx3acHcm3cv3tD08oQRqwL" +
            "DNsK4r/7v/9zfv5//DPe/W9/yD5YAoVX+x1fvHnDm9MdgxqeP/W1x+M4UqWnNTZ4DtMzxBgC6+VK" +
            "WRfKfOEyfWIc7jicfkKOZ+L512hZqWmlxCu29rE0o4KogPTVncY6jHG96i6pswf0Hd7GOGq64aR0" +
            "NGUMuN2eSucNa2uUBk1cb61qQ8Rh1aKytV2N3kI4/+NTaOt9Qt2CWFS1vjDoffCxorZTQdSKsQYj" +
            "QpO2Nb3tvyKrjFHypkZWARXdPsXChw8f+dnv/AScYlrheDwQ8Bjv+Yt//2dk94r68YHD/ogT4fDq" +
            "jhwjh9uFdbrizjecsZ0paY0hDAxO0aaQSscfU8aPA7JG5vMjw/FAyXC9fCAg7PYnRCylVmqcoClW" +
            "Aupsf/jzjbzdEPV7Gq1B3ZAihZZAc8Y6j7pjzzBbjlLRLSsJOZd+DdVCk04ql9aVDS/YsMqPrELH" +
            "ceR2uyEiVUSyqsnGmKZqNuK1fX+/5ZIxRvsRFP2era6tou2FoegRLLl0dMHoRjP1vq/UTtfMc6Tl" +
            "wt3hiFfBSy/Rx/EN1Q3U60fSNWGHQLRP/Pzv/B6rFL57/1uM65NJ0zzz+WGktcrxeMLRCyWhYq1l" +
            "iZHyXHl1fIW4HUUa3uxoDloZ0TShdUaLR0Ojlhstu07qOt/V4DRS7BpZnNlU451xL60hEhjGezBh" +
            "aztc/1C0Rsl9fl8qnUt86aOt7fvq1GGt21qt+uPvwK0aLcbo4pydvPfJe9eMMa02kYxgynYHbnkc" +
            "oCIY6Qw8VF6I/C5E65+qF25YBJwLtJqJtfLrv/iKv/e3fo9Gw3rfe6mUWB+eWSXyh3/77/Dx/TMf" +
            "3n/L+t3MpWaoiWHc4y+3XoikQkypzwHWStGGk7apxgyaK6oQWyVdLxhGknuNl/4gbTixzI+09Tuk" +
            "LPjTFxg1WOs6E1ELTruNeduGN5sURIQUl846+LDRQQMmjBjjcK2RWkHFYiRQTfmeyH5ZSWaMBRXa" +
            "RiCXmn/cHfhShapq6nhoeB7H3TSOu+J9wFi7gc9b71Lr918dI93ETi/IhfRCp5SOZRpjOjPdGmIM" +
            "DeVynSjSWEslG8MUY2fuC9SYUYXDmy85vnvD/nCCmDl//TXpfCF4y14dbnsQwQdSKn0SqeVtgUCf" +
            "QTBWCfsDUEnrRM79TlpTxdiRQqPUZ1K6oljEemiV2jpI30rdTl9fXLusM3GdWC9nWqlgPepH7LDH" +
            "hqHbz9XSh0ldQFRIedowVo+o7cFPaWv+t4KwgVHL/w+K90N4AQIYnQAAAABJRU5ErkJggg==";

    private Thread renderThread = new Thread(this::renderThreadFunc);

    private boolean running = true;

    private void initWindow() {
        GLFWErrorCallback.createPrint(System.err).set();

        final byte[] icon = Base64.getDecoder().decode(ICON);

        long glfwInitBegin = System.nanoTime();
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }
        long glfwInitEnd = System.nanoTime();

        if (glfwInitEnd - glfwInitBegin > 1e9) {
            LogManager.getLogger().fatal("WARNING : glfwInit took {} seconds to start.", (glfwInitEnd-glfwInitBegin) / 1.0e9);
        }

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_CLIENT_API, GLFW_OPENGL_API);
        glfwWindowHint(GLFW_CONTEXT_CREATION_API, GLFW_NATIVE_CONTEXT_API);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 2);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 0);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_ANY_PROFILE);
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        window = glfwCreateWindow(screenWidth, screenHeight, "FML early loading progress", NULL, NULL);
        if (window == NULL) {
            throw new RuntimeException("Failed to create the GLFW window"); // ignore it and make the GUI optional?
        }

        try (MemoryStack stack = stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1);
            IntBuffer pHeight = stack.mallocInt(1);
            IntBuffer monPosLeft = stack.mallocInt(1);
            IntBuffer monPosTop = stack.mallocInt(1);
            glfwGetWindowSize(window, pWidth, pHeight);
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
            glfwGetMonitorPos(glfwGetPrimaryMonitor(), monPosLeft, monPosTop);
            // Center the window
            glfwSetWindowPos(
                    window,
                    (vidmode.width() - pWidth.get(0)) / 2 + monPosLeft.get(0),
                    (vidmode.height() - pHeight.get(0)) / 2 + monPosTop.get(0)
            );
            IntBuffer iconWidth = stack.mallocInt(1);
            IntBuffer iconHeight = stack.mallocInt(1);
            IntBuffer iconChannels = stack.mallocInt(1);
            final GLFWImage.Buffer glfwImages = GLFWImage.mallocStack(1, stack);
            final ByteBuffer iconBuf = stack.malloc(icon.length);
            iconBuf.put(icon);
            iconBuf.position(0);
            final ByteBuffer imgBuffer = STBImage.stbi_load_from_memory(iconBuf, iconWidth, iconHeight, iconChannels, 4);
            if (imgBuffer == null) {
                throw new RuntimeException("Failed to load window icon"); // ignore it and make the icon optional?
            }
            glfwImages.position(0);
            glfwImages.width(iconWidth.get(0));
            glfwImages.height(iconHeight.get(0));
            imgBuffer.position(0);
            glfwImages.pixels(imgBuffer);
            glfwImages.position(0);
            glfwSetWindowIcon(window, glfwImages);
        }
        glfwShowWindow(window);
        glfwPollEvents();
    }

    private void renderProgress() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(0.0D, screenWidth, screenHeight, 0.0D, -1000.0D, 1000.0D);
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();

        glEnableClientState(GL11.GL_VERTEX_ARRAY);
        glEnable(GL_BLEND);
        renderMessages();
        glfwSwapBuffers(window);
    }

    private static float clamp(float num, float min, float max) {
        if (num < min) {
            return min;
        } else {
            return num > max ? max : num;
        }
    }

    private static int clamp(int num, int min, int max) {
        if (num < min) {
            return min;
        } else {
            return num > max ? max : num;
        }
    }

    private static int hsvToRGB(float hue, float saturation, float value) {
        int i = (int)(hue * 6.0F) % 6;
        float f = hue * 6.0F - (float)i;
        float f1 = value * (1.0F - saturation);
        float f2 = value * (1.0F - f * saturation);
        float f3 = value * (1.0F - (1.0F - f) * saturation);
        float f4;
        float f5;
        float f6;
        switch(i) {
            case 0:
                f4 = value;
                f5 = f3;
                f6 = f1;
                break;
            case 1:
                f4 = f2;
                f5 = value;
                f6 = f1;
                break;
            case 2:
                f4 = f1;
                f5 = value;
                f6 = f3;
                break;
            case 3:
                f4 = f1;
                f5 = f2;
                f6 = value;
                break;
            case 4:
                f4 = f3;
                f5 = f1;
                f6 = value;
                break;
            case 5:
                f4 = value;
                f5 = f1;
                f6 = f2;
                break;
            default:
                throw new RuntimeException("Something went wrong when converting from HSV to RGB. Input was " + hue + ", " + saturation + ", " + value);
        }

        int j = clamp((int)(f4 * 255.0F), 0, 255);
        int k = clamp((int)(f5 * 255.0F), 0, 255);
        int l = clamp((int)(f6 * 255.0F), 0, 255);
        return j << 16 | k << 8 | l;
    }

    private void renderMessages() {
        List<Pair<Integer, StartupMessageManager.Message>> messages = StartupMessageManager.getMessages();
        for (int i = 0; i < messages.size(); i++) {
            final Pair<Integer, StartupMessageManager.Message> pair = messages.get(i);
            final float fade = clamp((4000.0f - (float) pair.getLeft() - ( i - 4 ) * 1000.0f) / 5000.0f, 0.0f, 1.0f);
            if (fade <0.01f) continue;
            StartupMessageManager.Message msg = pair.getRight();
            renderMessage(msg.getText(), msg.getTypeColour(), ((screenHeight - 15) / 20) - i, fade);
        }
        renderMemoryInfo();
    }

    private static final float[] memorycolour = new float[] { 0.0f, 0.0f, 0.0f};

    private void renderMemoryInfo() {
        final MemoryUsage heapusage = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
        final MemoryUsage offheapusage = ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage();
        final float pctmemory = (float) heapusage.getUsed() / heapusage.getMax();
        String memory = String.format("Memory Heap: %d / %d MB (%.1f%%)  OffHeap: %d MB", heapusage.getUsed() >> 20, heapusage.getMax() >> 20, pctmemory * 100.0, offheapusage.getUsed() >> 20);

        final int i = hsvToRGB((1.0f - (float)Math.pow(pctmemory, 1.5f)) / 3f, 1.0f, 0.5f);
        memorycolour[2] = ((i) & 0xFF) / 255.0f;
        memorycolour[1] = ((i >> 8 ) & 0xFF) / 255.0f;
        memorycolour[0] = ((i >> 16 ) & 0xFF) / 255.0f;
        renderMessage(memory, memorycolour, 1, 1.0f);
    }

    private void renderMessage(final String message, final float[] colour, int row, float alpha) {
        ByteBuffer charBuffer = MemoryUtil.memAlloc(message.length() * 270);
        int quads = STBEasyFont.stb_easy_font_print(0, 0, message, null, charBuffer);

        glVertexPointer(3, GL11.GL_FLOAT, 16, charBuffer);
        glEnable(GL_BLEND);
        GL14.glBlendColor(0,0,0, alpha);
        glBlendFunc(GL14.GL_CONSTANT_ALPHA, GL14.GL_ONE_MINUS_CONSTANT_ALPHA);
        glColor3f(colour[0], colour[1], colour[2]);
        glPushMatrix();
        glTranslatef(10, row * 20, 0);
        glScalef(2, 2, 1);
        glDrawArrays(GL11.GL_QUADS, 0, quads * 4);
        glPopMatrix();
        MemoryUtil.memFree(charBuffer);
    }

    private void renderThreadFunc() {
        glfwMakeContextCurrent(window);
        glfwSwapInterval(1);
        GL.createCapabilities();
        glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT);
        while (running) {
            renderProgress();
            try {
                Thread.sleep(50);
            } catch (InterruptedException ignored) {
                break;
            }
        }
        glfwMakeContextCurrent(0);
    }

    @Override
    public Runnable start() {
        initWindow();
        renderThread.start();
        return org.lwjgl.glfw.GLFW::glfwPollEvents;
    }

    @Override
    public long handOffWindow(final IntSupplier width, final IntSupplier height, final Supplier<String> title, final LongSupplier monitorSupplier) {
        running = false;
        try {
            renderThread.join();
        } catch (InterruptedException ignored) {
        }
        glfwSetWindowTitle(window, title.get());
        glfwSetWindowSize(window, width.getAsInt(), height.getAsInt());
        if (monitorSupplier.getAsLong() != 0L)
            glfwSetWindowMonitor(window, monitorSupplier.getAsLong(), 0, 0, width.getAsInt(), height.getAsInt(), GLFW_DONT_CARE);
        glfwMakeContextCurrent(window);
        GL.createCapabilities();
        glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT);
        renderProgress();
        glfwSwapInterval(0);
        glfwSwapBuffers(window);
        glfwSwapInterval(1);
        return window;
    }

    @Override
    public boolean replacedWindow() {
        return true;
    }
}
