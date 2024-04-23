package dominio;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import dominio.ImaJ.ImaJ;
import dominio.ImaJ.Properties;
import persistencia.ImageReader;
import visao.ImageShow;

public class Processor {

	public List<Entity> process(File file) {
		ImageShow imageShow = new ImageShow();
		
		ArrayList<Entity> list = new ArrayList<>();
		int[][][] im = ImageReader.imRead(file.getPath());
		
		int[][][] im_blur = ImaJ.imGaussian(im, 5);

		int[][] im_gray = ImaJ.rgb2gray(im_blur);


		int[][] im_red = ImaJ.splitChannel(im_blur, 0);
		imageShow.imShow(im_red, file.getPath());

		int[][] im_green = ImaJ.splitChannel(im_blur, 1);
		imageShow.imShow(im_green, file.getPath());

		int[][] im_blue = ImaJ.splitChannel(im_blur, 2);
		imageShow.imShow(im_blue, file.getPath());
		
		
		boolean[][] im_mask = ImaJ.im2bw(im_red);
		imageShow.imShow(im_mask, file.getPath());
		
		ArrayList<Properties> sementes = ImaJ.regionProps(im_mask);
		
		for(int i = 0; i < sementes.size(); i++) {
			int[][][] im2 = ImaJ.imCrop(im, sementes.get(i).boundingBox[0], sementes.get(i).boundingBox[1], 
					                        sementes.get(i).boundingBox[2], sementes.get(i).boundingBox[3]);
			 
			// Aplicando máscara na imagem original
			for(int x = 0; x < im2.length; x++) {
				for(int y = 0; y < im2[0].length; y++) {
					//Se é pixel de fundo
					if(!sementes.get(i).image[x][y]) {
						im2[x][y] = new int[]{0,0,0};
					}
				}
			}
			ImageReader.imWrite(im2, file.getPath().split("\\.")[0] + "_" + i + ".png");
			
			list.add(new Entity(sementes.get(i).area, 1, file.getPath().split("\\.")[0] + "_" + i + ".png", "grande"));			
		}
		
		return list;
	}
}