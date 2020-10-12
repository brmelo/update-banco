export class Conta {
    id: number;
    nome: string;
    cpf: string;
    saldo: number;
    conta: number;
    data: Date;

constructor(nome: string, cpf: string, saldo: number){
    this.nome = nome;
    this.cpf = cpf;
    this.saldo = saldo;
}

}