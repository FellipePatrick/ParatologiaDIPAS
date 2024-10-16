package com.api.sic.backend.domain;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import com.api.sic.backend.domain.enumerates.StatusChamado;

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

@EqualsAndHashCode(callSuper = true)
@SQLDelete(sql = "UPDATE chamado SET deleted_at = CURRENT_TIMESTAMP WHERE id=?")
@SQLRestriction("deleted_at is null")
@Entity
@Table(name = "chamado")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Chamado extends AbstractEntity{
    private String assunto;
    private String descricao;
    @Enumerated(EnumType.STRING)
    private StatusChamado status = StatusChamado.ABERTO;
    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "id_dono", referencedColumnName = "id")
    private Usuario dono;
}
