package cog.com.sic.frontend.dto;

import java.util.List;

public class UsuarioPagedResponseDTO {
    private List<UsuarioResponseDTO> content;


    public List<UsuarioResponseDTO> getContent() {
        return content;
    }

    public void setContent(List<UsuarioResponseDTO> content) {
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
