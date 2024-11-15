package cog.com.sic.frontend.dto.imagem;

import java.util.List;

public class ImagemPagedResponseDTO {
    private List<ImagemRequestDTO> content; // Lista de imagens
    private int totalPages;
    private long totalElements;

    // Getters e Setters
    public List<ImagemRequestDTO> getContent() {
        return content;
    }

    public void setContent(List<ImagemRequestDTO> content) {
        this.content = content;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }
}
