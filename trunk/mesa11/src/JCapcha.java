import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import com.octo.captcha.service.image.DefaultManageableImageCaptchaService;

public class JCapcha {

	public void gerar() {
	}

	public static void main(String[] args) throws IOException {
		ByteArrayOutputStream jpegstream = new ByteArrayOutputStream();
		DefaultManageableImageCaptchaService defaultManageableImageCaptchaService = new DefaultManageableImageCaptchaService();
		BufferedImage challenge = defaultManageableImageCaptchaService
				.getImageChallengeForID("11");
		// JPEGImageEncoder jpegencoderEncoder = JPEGCodec
		// .createJPEGEncoder(jpegstream);
		// jpegencoderEncoder.encode(challenge);
		ImageIO.write(challenge, "jpg", jpegstream);
		JLabel jLabel = new JLabel(new ImageIcon(jpegstream.toByteArray()));

		Boolean validateResponseForID = defaultManageableImageCaptchaService
				.validateResponseForID("11", JOptionPane.showInputDialog(null,
						jLabel));
		JOptionPane.showMessageDialog(null, "Resultado é "
				+ validateResponseForID);

	}
}
