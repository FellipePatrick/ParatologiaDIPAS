package cog.com.sic.frontend.dto.chamado;

import java.util.List;

public class ChamadoPagedResponseDTO {
    private List<ChamadoResponseDTO> content;
    public List<ChamadoResponseDTO> getContent() {
        return content;
    }

    public void setContent(List<ChamadoResponseDTO> content) {
        this.content = content;
    }
}
class Pageable {
    private int pageNumber;
    private int pageSize;

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}
