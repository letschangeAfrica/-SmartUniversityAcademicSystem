package smartuniversityacademicsystem.util;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeWriter;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import javafx.scene.image.Image;
import javax.imageio.ImageIO;

/**
 * Utility class for generating and decoding QR codes using ZXing.
 *
 * QR data format used in this app:
 *   SUAS|STUDENT|{userId}|{fullName}|{username}
 *
 * Example:
 *   SUAS|STUDENT|7|Alice Johnson|alice
 */
public class QRCodeUtil {

    private static final String PREFIX = "SUAS|STUDENT|";

    // ── Generate ──────────────────────────────────────────────────────────────

    /**
     * Builds the data string for a student's QR code.
     */
    public static String buildStudentData(int userId, String fullName, String username) {
        return PREFIX + userId + "|" + fullName + "|" + username;
    }

    /**
     * Generates a QR code as a JavaFX Image ready to drop into an ImageView.
     *
     * @param data   The string to encode.
     * @param size   Width and height in pixels (square).
     * @return       JavaFX Image, or null if generation failed.
     */
    public static Image generateQRCode(String data, int size) {
        try {
            QRCodeWriter writer = new QRCodeWriter();
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.MARGIN, 1);
            BitMatrix matrix = writer.encode(data, BarcodeFormat.QR_CODE, size, size, hints);
            BufferedImage buffered = MatrixToImageWriter.toBufferedImage(matrix);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(buffered, "PNG", baos);
            return new Image(new ByteArrayInputStream(baos.toByteArray()));
        } catch (WriterException | IOException e) {
            System.err.println("QR generation failed: " + e.getMessage());
            return null;
        }
    }

    /**
     * Saves a QR code PNG to the given file path.
     *
     * @param data   The string to encode.
     * @param size   Width and height in pixels.
     * @param dest   Target file (should end in .png).
     */
    public static void saveQRCode(String data, int size, File dest) throws Exception {
        QRCodeWriter writer = new QRCodeWriter();
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.MARGIN, 1);
        BitMatrix matrix = writer.encode(data, BarcodeFormat.QR_CODE, size, size, hints);
        MatrixToImageWriter.writeToPath(matrix, "PNG", dest.toPath());
    }

    // ── Decode ────────────────────────────────────────────────────────────────

    /**
     * Decodes a QR code image file and returns the embedded string.
     *
     * @param imageFile  PNG/JPG file containing the QR code.
     * @return           Decoded string, or null if decoding failed.
     */
    public static String decodeQRCode(File imageFile) {
        try {
            BufferedImage buffered = ImageIO.read(imageFile);
            if (buffered == null) return null;
            BinaryBitmap bitmap = new BinaryBitmap(
                new HybridBinarizer(new BufferedImageLuminanceSource(buffered))
            );
            Result result = new MultiFormatReader().decode(bitmap);
            return result.getText();
        } catch (NotFoundException e) {
            return null; // no QR code found in image
        } catch (IOException e) {
            System.err.println("Could not read image: " + e.getMessage());
            return null;
        }
    }

    // ── Parse ─────────────────────────────────────────────────────────────────

    /**
     * Parses a decoded QR string into its components.
     * Returns null if the format is not a valid SUAS student QR.
     *
     * @return int[3] = { userId, fullName-index (unused), username-index }
     *         Use parseUsername / parseUserId helpers instead.
     */
    public static boolean isValidStudentQR(String decoded) {
        return decoded != null && decoded.startsWith(PREFIX) && decoded.split("\\|").length == 5;
    }

    /** Extracts the user ID from a decoded QR string. */
    public static int parseUserId(String decoded) {
        return Integer.parseInt(decoded.split("\\|")[2]);
    }

    /** Extracts the full name from a decoded QR string. */
    public static String parseFullName(String decoded) {
        return decoded.split("\\|")[3];
    }

    /** Extracts the username from a decoded QR string. */
    public static String parseUsername(String decoded) {
        return decoded.split("\\|")[4];
    }
}
