import { Component, OnInit } from '@angular/core';
import { Conta } from './conta';
import { ContaService } from '../conta.service';

import { FormBuilder, FormGroup, Validators } from '@angular/forms'

@Component({
  selector: 'app-conta',
  templateUrl: './conta.component.html',
  styleUrls: ['./conta.component.css']
})
export class ContaComponent implements OnInit {

  formulario: FormGroup;
  contas: Conta[] = [];
  colunas = ['nome', 'cpf', 'saldo'];

  constructor(
    private service: ContaService,
    private formb: FormBuilder
  ) { }

  ngOnInit(): void {
    this.montarFormulario();
    this.listarContas();

  }

  montarFormulario(){
    this.formulario = this.formb.group({
      nome: [],
      cpf: ['', Validators.required],
      saldo: ['', Validators.required]
    })
  }

  listarContas() {
    this.service.listar().subscribe(resposta => {
      this.contas = resposta;
    })
  }

  submit() {
    //const erroCpf = this.formulario.controls.cpf.errors.required;
    //const erroSaldo = this.formulario.controls.saldo.errors.required;
    const isValid = this.formulario.valid;

    const formValues = this.formulario.value;
    const conta: Conta = new Conta(formValues.nome, formValues.cpf, formValues.saldo);

    this.service.salvar(conta).subscribe(resposta => {
      this.contas.push(resposta);
      console.log(this.contas);
    })
  }

}
