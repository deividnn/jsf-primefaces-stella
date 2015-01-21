/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stella.stella;

import br.com.caelum.stella.boleto.Banco;
import br.com.caelum.stella.boleto.Beneficiario;
import br.com.caelum.stella.boleto.Boleto;
import br.com.caelum.stella.boleto.Datas;
import br.com.caelum.stella.boleto.Endereco;
import br.com.caelum.stella.boleto.Pagador;
import br.com.caelum.stella.boleto.bancos.BancoDoBrasil;
import br.com.caelum.stella.boleto.bancos.Bradesco;
import br.com.caelum.stella.boleto.bancos.Caixa;
import br.com.caelum.stella.boleto.bancos.Santander;
import br.com.caelum.stella.boleto.transformer.GeradorDeBoleto;
import br.com.caelum.stella.inwords.FormatoDeReal;
import br.com.caelum.stella.inwords.NumericToWordsConverter;
import br.com.caelum.stella.validation.CNPJValidator;
import br.com.caelum.stella.validation.CPFValidator;
import br.com.caelum.stella.validation.InvalidStateException;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author deivid
 */
@ManagedBean
@ViewScoped
public class Bean implements Serializable {

    private BigDecimal valor;
    private String extenso;
    private String cpf;
    private String avisoCPF;
    private String cnpj;
    private String avisoCNPJ;
    private String boleto;
    private String carne;

    public void porExtenso() {
        NumericToWordsConverter converter;
        converter = new NumericToWordsConverter(new FormatoDeReal());
        double numero = this.valor.doubleValue();
        this.extenso = converter.toWords(numero);

    }

    public void validarCPF() {
        CPFValidator validator = new CPFValidator();
        try {
            // lógica de negócio ...
            validator.assertValid(this.cpf);
            this.avisoCPF = "cpf valido";
            // continuação da lógica de negócio ...
        } catch (InvalidStateException e) { // exception lançada quando o documento é inválido
            this.avisoCPF = "CPF invalido:" + e;
        }
    }

    public void validarCNPJ() {
        CNPJValidator validator = new CNPJValidator();
        try {
            // lógica de negócio ...
            validator.assertValid(this.cnpj);
            this.avisoCNPJ = "cnpj valido";
            // continuação da lógica de negócio ...
        } catch (InvalidStateException e) { // exception lançada quando o documento é inválido
            this.avisoCNPJ = "CNPJ invalido:" + e;
        }
    }

    public void validarCPNJCPF() {
        validarCPF();
        validarCNPJ();
    }

    public void gerarBoleto() {
        Datas datas = Datas.novasDatas()
                .comDocumento(1, 5, 2008)
                .comProcessamento(1, 5, 2008)
                .comVencimento(2, 1, 2015);

        Endereco enderecoBeneficiario = Endereco.novoEndereco()
                .comLogradouro("Av das Empresas, 555")
                .comBairro("Bairro Grande")
                .comCep("01234-555")
                .comCidade("São Paulo")
                .comUf("SP");

        //Quem emite o boleto
        Beneficiario beneficiario = Beneficiario.novoBeneficiario()
                .comNomeBeneficiario("Fulano de Tal")
                .comAgencia("1824").comDigitoAgencia("4")
                .comCodigoBeneficiario("76000")
                .comDigitoCodigoBeneficiario("5")
                .comNumeroConvenio("1207113")
                .comCarteira("18")
                .comEndereco(enderecoBeneficiario)
                .comNossoNumero("9000206");

        Endereco enderecoPagador = Endereco.novoEndereco()
                .comLogradouro("Av dos testes, 111 apto 333")
                .comBairro("Bairro Teste")
                .comCep("01234-111")
                .comCidade("São Paulo")
                .comUf("SP");

        //Quem paga o boleto
        Pagador pagador = Pagador.novoPagador()
                .comNome("Fulano da Silva")
                .comDocumento("111.222.333-12")
                .comEndereco(enderecoPagador);

        Banco banco = new BancoDoBrasil();

        Boleto boleto2 = Boleto.novoBoleto()
                .comBanco(banco)
                .comDatas(datas)
                .comBeneficiario(beneficiario)
                .comPagador(pagador)
                .comValorBoleto("2036.47")
                .comNumeroDoDocumento("1234")
                .comInstrucoes("instrucao 1", "instrucao 2", "instrucao 3", "instrucao 4", "instrucao 5")
                .comLocaisDePagamento("local 1", "local 2");

        GeradorDeBoleto gerador = new GeradorDeBoleto(boleto2);
        File pdf = new File(System.getProperty("user.home") + File.separator + "brasil.pdf");

        gerador.geraPDF(pdf.getAbsolutePath());

        ServletContext servletContext = (ServletContext) FacesContext.
                getCurrentInstance().getExternalContext().getContext();
        String absoluteDiskPath = servletContext.getRealPath("/resources/arquivos");
        File a = new File(absoluteDiskPath + File.separator + "brasil.pdf");
        byte[] bytes = gerador.geraPDF();
        try {
            FileUtils.writeByteArrayToFile(a, bytes);
        } catch (IOException ex) {
            Logger.getLogger(Bean.class.getName()).log(Level.SEVERE, null, ex);
        }

        this.boleto = pdf.getAbsolutePath();
    }

    public void gerarCarne() {
        List<Boleto> boletos = new ArrayList<>();
        for (int i = 0; i < 10; i++) {

            Datas datas = Datas.novasDatas()
                    .comDocumento(1, 5, 2008)
                    .comProcessamento(1, 5, 2008)
                    .comVencimento(2, 1, 2015);

            Endereco enderecoBeneficiario = Endereco.novoEndereco()
                    .comLogradouro("Av das Empresas, 555")
                    .comBairro("Bairro Grande")
                    .comCep("01234-555")
                    .comCidade("São Paulo")
                    .comUf("SP");

            //Quem emite o boleto
            Beneficiario beneficiario = Beneficiario.novoBeneficiario()
                    .comNomeBeneficiario("Fulano de Tal")
                    .comAgencia("3614")
                    .comCodigoBeneficiario("01034602")
                    .comDigitoCodigoBeneficiario("4")
                    .comNumeroConvenio("1207113")
                    .comCarteira("18")
                    .comEndereco(enderecoBeneficiario)
                    .comNossoNumero("9000206");

            Endereco enderecoPagador = Endereco.novoEndereco()
                    .comLogradouro("Av dos testes, 111 apto 333")
                    .comBairro("Bairro Teste")
                    .comCep("01234-111")
                    .comCidade("São Paulo")
                    .comUf("SP");

            //Quem paga o boleto
            Pagador pagador = Pagador.novoPagador()
                    .comNome("Fulano da Silva")
                    .comDocumento("111.222.333-12")
                    .comEndereco(enderecoPagador);

            Banco banco = new BancoDoBrasil();

            Boleto boleto2 = Boleto.novoBoleto()
                    .comBanco(banco)
                    .comDatas(datas)
                    .comBeneficiario(beneficiario)
                    .comPagador(pagador)
                    .comValorBoleto("2036.47")
                    .comNumeroDoDocumento("1234" + i)
                    .comInstrucoes("instrucao 1", "instrucao 2", "instrucao 3", "instrucao 4", "instrucao 5")
                    .comLocaisDePagamento("local 1", "local 2");
            boletos.add(boleto2);

        }

        GeradorDeBoleto gerador = new GeradorDeBoleto(boletos);

        File pdf = new File(System.getProperty("user.home") + File.separator + "carne.pdf");

        gerador.geraPDF(pdf.getAbsolutePath());

        ServletContext servletContext = (ServletContext) FacesContext.
                getCurrentInstance().getExternalContext().getContext();
        String absoluteDiskPath = servletContext.getRealPath("/resources/arquivos");
        File a = new File(absoluteDiskPath + File.separator + "carne.pdf");
        byte[] bytes = gerador.geraPDF();
        try {
            FileUtils.writeByteArrayToFile(a, bytes);
        } catch (IOException ex) {
            Logger.getLogger(Bean.class.getName()).log(Level.SEVERE, null, ex);
        }

        this.carne = pdf.getAbsolutePath();
    }

    public void gerarBoletocaixa() {
        Datas datas = Datas.novasDatas().comDocumento(22, 04, 2013)
                .comProcessamento(22, 04, 2013).comVencimento(29, 04, 2013);

        Beneficiario beneficiario = Beneficiario.novoBeneficiario().comNomeBeneficiario("Rodrigo Turini")
                .comAgencia("2873").comCarteira("1")
                .comCodigoBeneficiario("2359").comNossoNumero("990000000003994458")
                .comDigitoNossoNumero("0");

        Pagador pagador = Pagador.novoPagador().comNome("Mario Amaral");
        Caixa banco = new Caixa();
        Boleto boletoc = Boleto.novoBoleto().comDatas(datas)
                .comBeneficiario(beneficiario).comBanco(banco).comPagador(pagador)
                .comValorBoleto(4016.10).comNumeroDoDocumento("3084373");

        GeradorDeBoleto gerador = new GeradorDeBoleto(boletoc);
        File pdf = new File(System.getProperty("user.home") + File.separator + "caixa.pdf");

        gerador.geraPDF(pdf.getAbsolutePath());

        ServletContext servletContext = (ServletContext) FacesContext.
                getCurrentInstance().getExternalContext().getContext();
        String absoluteDiskPath = servletContext.getRealPath("/resources/arquivos");
        File a = new File(absoluteDiskPath + File.separator + "caixa.pdf");
        byte[] bytes = gerador.geraPDF();
        try {
            FileUtils.writeByteArrayToFile(a, bytes);
        } catch (IOException ex) {
            Logger.getLogger(Bean.class.getName()).log(Level.SEVERE, null, ex);
        }

        this.boleto = pdf.getAbsolutePath();
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public String getExtenso() {
        return extenso;
    }

    public void setExtenso(String extenso) {
        this.extenso = extenso;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getAvisoCPF() {
        return avisoCPF;
    }

    public void setAvisoCPF(String avisoCPF) {
        this.avisoCPF = avisoCPF;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public String getAvisoCNPJ() {
        return avisoCNPJ;
    }

    public void setAvisoCNPJ(String avisoCNPJ) {
        this.avisoCNPJ = avisoCNPJ;
    }

    public String getBoleto() {
        return boleto;
    }

    public void setBoleto(String boleto) {
        this.boleto = boleto;
    }

    public String getCarne() {
        return carne;
    }

    public void setCarne(String carne) {
        this.carne = carne;
    }

   
}
