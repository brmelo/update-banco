import { Component, OnInit } from '@angular/core';
import { Conta } from './conta';
import { ContaService } from '../conta.service';

import { FormBuilder, FormGroup, Validators } from '@angular/forms'
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-conta',
  templateUrl: './conta.component.html',
  styleUrls: ['./conta.component.css']
})
export class ContaComponent implements OnInit {

  formulario: FormGroup;
  contas: Conta[] = [];
  colunas = ['nome', 'cpf', 'conta', 'saldo'];

  constructor(
    private service: ContaService,
    private formb: FormBuilder,
    private snackBar: MatSnackBar
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
    const formValues = this.formulario.value;
    const conta: Conta = new Conta(formValues.nome, formValues.cpf, formValues.saldo);

    this.service.salvar(conta).subscribe(resposta => {
      this.listarContas();
      this.snackBar.open('Conta cadastrada', resposta.conta.toString() , {duration: 5000})
      this.formulario.reset();
      //let lista: Conta[] = [...this.contas, resposta]
      //this.contas = lista;
    })
  }

}
