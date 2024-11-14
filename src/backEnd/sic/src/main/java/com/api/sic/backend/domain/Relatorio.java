package com.api.sic.backend.domain;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import com.api.sic.backend.domain.enumerates.StatusRelatorio;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@SQLDelete(sql = "UPDATE relatorio SET deleted_at = CURRENT_TIMESTAMP WHERE id=?")
@SQLRestriction("deleted_at is null")
@Entity
@Table(name = "relatorio")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Relatorio extends AbstractEntity {
    private String titulo;
    private String descricao;
    @Enumerated(EnumType.STRING)
    private StatusRelatorio status;
    private LocalDateTime dataModificacao;
    
    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "id_usuario", referencedColumnName = "id")
    private Usuario usuario;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "id_gestor", referencedColumnName = "id")
    private Usuario gestor;
}
