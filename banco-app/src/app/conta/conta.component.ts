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

  constructor(
    private service: ContaService,
    private formb: FormBuilder
  ) { }

  ngOnInit(): void {

this.formulario = this.formb.group({
   nome: ['', Validators.required],
    cpf: ['', Validators.required],
  saldo: ['', Validators.required]
})

  }

  submit(){
    const formValues = this.formulario.value;
    const conta: Conta = new Conta(formValues.nome, formValues.cpf, formValues.saldo);

    this.service.salvar(conta).subscribe(resposta => {
      this.contas.push(resposta);
      console.log(this.contas);
    })
  }

}
