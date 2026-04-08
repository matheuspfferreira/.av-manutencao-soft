public class MatriculaService {
    private MatriculaRepository repo;
    private PagamentoService pagamentoService;
    private NotificadorEmail emailService;

    public MatriculaService(MatriculaRepository repo,
                            PagamentoService pagamentoService,
                            NotificadorEmail emailService) {
        this.repo = repo;
        this.pagamentoService = pagamentoService;
        this.emailService = emailService;
    }

    public String processarMatricula(Aluno aluno, Disciplina disciplina, boolean bolsa, boolean periodoAjuste, boolean documentosOk) {
        if (aluno == null) return "Aluno inválido";
        if (disciplina == null) return "Disciplina inválida";
        if (repo.jaMatriculado(aluno.getRa(), disciplina.getCodigo())) return "Aluno já matriculado";
        if (!disciplina.temVagas()) return "Sem vagas";
        if (!documentosOk) return "Documentação pendente";
        if(!aluno.isAdimplente()) return "Aluno inadimplente";

        double valor = disciplina.getValor();
        if (bolsa) {
            valor = valor * 0.5;
        }
        if (periodoAjuste) {
            valor = valor + 20;
        }

        Matricula matricula = new Matricula(aluno, disciplina, valor, true);
        repo.salvar(matricula);
        emailService.enviarConfirmacao(aluno, disciplina);
        disciplina.reduzirVaga();

        return "Matrícula concluída";
    }
}

